package client;

import models.Organization;
import shared.ClientRequest;
import client.utils.InputHelper;
import java.util.Scanner;

public class ClientConsole {
    private final Scanner scanner;

    public ClientConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Считывает команду из консоли и формирует запрос для отправки на сервер.
     * @return готовый ClientRequest или null, если введён "exit"
     */
    public ClientRequest readCommand() {
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;

        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        // Команды без аргументов
        if (cmd.equals("exit")) return null; // Сигнал на завершение
        if (cmd.equals("help") || cmd.equals("info") || cmd.equals("show") ||
                cmd.equals("clear") || cmd.equals("history")) {
            return new ClientRequest(cmd);
        }

        // Команды с аргументом-строкой (ID)
        if (cmd.equals("remove_key")) {
            return new ClientRequest(cmd, arg);
        }

        // Команда insert: читаем объект организации
        if (cmd.equals("insert")) {
            try {
                Organization org = readOrganization();
                return new ClientRequest(cmd, org);
            } catch (Exception e) {
                System.err.println("Ошибка ввода: " + e.getMessage());
                return null;
            }
        }

        // Команда server_save (только для сервера, но клиент может отправить)
        if (cmd.equals("server_save")) {
            return new ClientRequest(cmd);
        }

        System.out.println("Неизвестная команда. Введите 'help'.");
        return null;
    }

    /**
     * Считывает данные для новой организации через InputHelper.
     */
    private Organization readOrganization() {
        Organization org = new Organization();

        System.out.println("=== Ввод организации (введите 'отмена' для отмены) ===");

        org.setName(InputHelper.readString(scanner, "Название: ", false));

        long x = InputHelper.readLong(scanner, "Координата X (> -410): ", -410, null);
        double y = InputHelper.readDouble(scanner, "Координата Y: ");
        org.setCoordinates(new models.Coordinates(x, y));

        org.setAnnualTurnover(InputHelper.readLong(scanner, "Годовой оборот (> 0): ", 0, null));
        org.setFullName(InputHelper.readString(scanner, "Полное название: ", true));

        org.setType(InputHelper.readEnum(scanner, "Тип организации (или пустая строка): ", models.OrganizationType.class));

        String street = InputHelper.readString(scanner, "Улица адреса: ", true);
        org.setPostalAddress(new models.Address(street));

        return org;
    }

    public void printHelp() {
        System.out.println("""
            Доступные команды:
            help                       — показать эту справку
            info                       — информация о коллекции
            show                       — показать все элементы (отсортировано по имени)
            insert                     — добавить новый элемент
            remove_key {id}            — удалить элемент по ID
            clear                      — очистить коллекцию
            server_save                — сохранить коллекцию на сервере (только сервер)
            history                    — показать историю команд
            exit                       — завершить работу клиента
            Ввод 'отмена' или 'cancel' на любом этапе отменяет текущую команду.
            """);
    }
}