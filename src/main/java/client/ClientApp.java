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
            System.out.println("Клиент подключён. Введите 'help' для справки.");

            boolean running = true;
            while (running) {
                ClientRequest request = console.readCommand();

                if (request == null) {
                    // Проверка на exit
                    running = false;
                    break;
                }

                // Отправка на сервер
                ServerResponse response = network.sendRequest(request);

                if (response == null) {
                    // Сервер недоступен — продолжаем цикл, клиент не падает
                    continue;
                }

                // Обработка ответа
                if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                    System.out.println(response.isSuccess() ? "✓ " : "✗ " + response.getMessage());
                }

                if (response.getData() != null && !response.getData().isEmpty()) {
                    response.getData().forEach(item -> System.out.println("  • " + item));
                }
            }

            System.out.println("Клиент завершил работу.");
        } catch (Exception e) {
            System.err.println("Критическая ошибка клиента: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}