package client;

import shared.ClientRequest;
import shared.ServerResponse;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class ClientNetwork implements AutoCloseable{
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private static final int TIMEOUT_MS = 3000;

    public ClientNetwork(String serverHost, int serverPort) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT_MS);
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
    }

    /**
     * Отправляет запрос и получает ответ от сервера.
     * @return ServerResponse или null, если сервер недоступен
     */
    public ServerResponse sendRequest(ClientRequest request) {
        try {
            // Сериализация запроса
            byte[] requestData = serialize(request);
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);
            socket.send(requestPacket);

            // Приём ответа
            byte[] responseData = new byte[65535];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            socket.receive(responsePacket);

            return deserialize(responsePacket.getData(), responsePacket.getLength());
        } catch (SocketTimeoutException e) {
            System.err.println("Сервер не ответил за " + TIMEOUT_MS + " мс. Проверьте соединение.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка сети: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
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