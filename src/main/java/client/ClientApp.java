package client;

import shared.ClientRequest;
import shared.ServerResponse;
import java.util.Scanner;

public class ClientApp {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

        try (ClientNetwork network = new ClientNetwork(SERVER_HOST, SERVER_PORT)) {
            ClientConsole console = new ClientConsole(scanner);
            System.out.println("Client connected. Type 'help' for commands.");

            while (true) {
                ClientRequest request = console.readCommand();

                if (request == null) {
                    // Команда exit
                    System.out.println("Client shutting down.");
                    break;
                }

                // Локальные команды (например, help)
                if (request.getCommandName().equals("__local__")) {
                    continue; // уже обработано в console.readCommand()
                }

                // Отправка на сервер
                ServerResponse response = network.sendRequest(request);
                if (response == null) {
                    System.err.println("Server not responding. Check if server is running.");
                    continue;
                }

                // Обработка ответа
                if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                    System.out.println(response.isSuccess() ? "✓ " + response.getMessage() : "✗ " + response.getMessage());
                }
                if (response.getData() != null && !response.getData().isEmpty()) {
                    response.getData().forEach(item -> System.out.println("  • " + item));
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal client error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}