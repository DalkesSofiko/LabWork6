package server;

import shared.ClientRequest;
import shared.ServerResponse;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ServerNetwork {
    private final DatagramChannel channel;
    private final CommandProcessor processor;
    private volatile boolean running = true;

    public ServerNetwork(int port, CommandProcessor processor) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false); // Неблокирующий режим
        this.channel.bind(new InetSocketAddress(port));
        this.processor = processor;
    }

    public void start() throws IOException, ClassNotFoundException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(65535);
        System.out.println(" Сервер запущен на порту " + channel.getLocalAddress());

        while (running) {
            buffer.clear();
            SocketAddress clientAddr = channel.receive(buffer);

            if (clientAddr != null) { // Данные есть
                buffer.flip();
                ClientRequest request = deserialize(buffer);
                ServerResponse response = processor.process(request);

                // Если команда сохранения, сохраняем файл, тут пусто, т.к все уже есть в request.getCommandName()
                if ("server_save".equals(request.getCommandName())) {}

                channel.send(serialize(response), clientAddr);
            }
            Thread.sleep(10);
        }
    }

    public void stop() { running = false; }

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