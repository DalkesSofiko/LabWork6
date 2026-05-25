package client;

import shared.ClientRequest;
import shared.ServerResponse;
import java.util.Scanner;

public class ClientApp {
    // Значения по умолчанию
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        // Парсим аргументы командной строки
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректный порт, используется значение по умолчанию: " + DEFAULT_PORT);
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

        try (ClientNetwork network = new ClientNetwork(host, port)) {
            ClientConsole console = new ClientConsole(scanner);
            System.out.println("Client connected to " + host + ":" + port + ". Type 'help' for commands.");

            while (true) {
                ClientRequest request = console.readCommand();

                if (request == null) {
                    System.out.println("Client shutting down.");
                    break;
                }

                if (request.getCommandName().equals("__local__")) {
                    continue;
                }

                ServerResponse response = network.sendRequest(request);
                if (response == null) {
                    System.err.println("Server not responding. Check if server is running.");
                    continue;
                }

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