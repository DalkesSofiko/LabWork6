package server;

import shared.ClientRequest;
import shared.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ServerNetwork {
    private static final Logger logger = LogManager.getLogger(ServerNetwork.class);

    private final DatagramChannel channel;
    private final CommandProcessor processor;
    private volatile boolean running = true;

    public ServerNetwork(int port, CommandProcessor processor) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false); // Неблокирующий режим
        this.channel.bind(new InetSocketAddress(port));
        this.processor = processor;
        logger.info("Сетевой модуль инициализирован на порту {}", port);
    }

    public void start() {
        ByteBuffer buffer = ByteBuffer.allocate(65535);
        logger.info("Сервер запущен и ожидает запросы на порту {}", channel.socket().getLocalPort());

        while (running) {
            try {
                buffer.clear();
                SocketAddress clientAddr = channel.receive(buffer);

                if (clientAddr != null) {
                    logger.debug("Получен UDP-пакет от {}", clientAddr);
                    buffer.flip();

                    ClientRequest request = deserialize(buffer);
                    logger.info("Получена команда '{}' от {}", request.getCommandName(), clientAddr);

                    ServerResponse response = processor.process(request);
                    logger.debug("Ответ для {}: success={}", clientAddr, response.isSuccess());

                    channel.send(serialize(response), clientAddr);
                    logger.info("Ответ отправлен клиенту {}", clientAddr);
                }

                // Небольшая задержка для снижения нагрузки на CPU в холостом цикле
                Thread.sleep(10);

            } catch (InterruptedException e) {
                logger.warn("Цикл приёма прерван", e);
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                logger.error("Ошибка ввода-вывода в сетевом цикле: {}", e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                logger.error("Ошибка десериализации запроса: {}", e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Неожиданная ошибка в сетевом модуле: {}", e.getMessage(), e);
            }
        }

        logger.info("Сетевой модуль остановлен");
        close();
    }

    public void stop() {
        logger.info("Получен сигнал остановки сети");
        running = false;
    }

    private void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                logger.debug("Канал DatagramChannel закрыт");
            }
        } catch (IOException e) {
            logger.error("Ошибка при закрытии канала: {}", e.getMessage(), e);
        }
    }

    private ClientRequest deserialize(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (ClientRequest) ois.readObject();
        }
    }

    private ByteBuffer serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }
}