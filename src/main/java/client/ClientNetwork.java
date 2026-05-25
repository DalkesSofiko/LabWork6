package client;

import shared.ClientRequest;
import shared.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class ClientNetwork implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(ClientNetwork.class);

    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private static final int TIMEOUT_MS = 3000;

    public ClientNetwork(String serverHost, int serverPort) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT_MS);
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        logger.info("Клиентский сетевой модуль инициализирован: сервер {}:{}", serverHost, serverPort);
    }

    /**
     * Отправляет запрос и получает ответ от сервера.
     * @return ServerResponse или null, если сервер недоступен
     */
    public ServerResponse sendRequest(ClientRequest request) {
        logger.debug("Отправка команды: {}", request.getCommandName());
        try {
            // Сериализация запроса
            byte[] requestData = serialize(request);
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);
            socket.send(requestPacket);
            logger.debug("Запрос отправлен на {}:{}", serverAddress, serverPort);

            // Приём ответа
            byte[] responseData = new byte[65535];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            socket.receive(responsePacket);
            logger.debug("Получен ответ от сервера, размер: {} байт", responsePacket.getLength());

            ServerResponse response = deserialize(responsePacket.getData(), responsePacket.getLength());
            logger.debug("Ответ сервера: success={}, message={}", response.isSuccess(), response.getMessage());
            return response;

        } catch (SocketTimeoutException e) {
            logger.warn("Сервер не ответил за {} мс. Проверьте соединение.", TIMEOUT_MS);
            return null;
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при обмене с сервером: {}", e.getMessage(), e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка десериализации ответа сервера: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            logger.debug("Сокет клиента закрыт");
        }
    }

    private byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    private ServerResponse deserialize(byte[] data, int length) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data, 0, length))) {
            return (ServerResponse) ois.readObject();
        }
    }
}