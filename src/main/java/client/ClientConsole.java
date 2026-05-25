package client;

import models.Organization;
import shared.ClientRequest;
import client.utils.InputHelper;
import client.utils.InputHelper.InputCancelledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class ClientConsole {
    private static final Logger logger = LogManager.getLogger(ClientConsole.class);
    private static final String LOCAL_CMD_MARKER = "__local__"; // маркер для команд, не требующих отправки

    private final Scanner scanner;

    public ClientConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public ClientRequest readCommand() {
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;

        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        // Локальная команда help
        if (cmd.equals("help")) {
            printHelp();
            return new ClientRequest(LOCAL_CMD_MARKER);
        }

        // Команда exit – завершение клиента
        if (cmd.equals("exit")) {
            logger.info("Получена команда exit, завершение клиента");
            return null;
        }

        // Команды без аргументов (отправляются на сервер)
        if (cmd.equals("info") || cmd.equals("show") || cmd.equals("clear") || cmd.equals("history")) {
            logger.debug("Команда без аргументов: {}", cmd);
            return new ClientRequest(cmd);
        }

        // Команды с аргументом-строкой (ID)
        if (cmd.equals("remove_key")) {
            if (arg == null || arg.isEmpty()) {
                System.out.println("Error: specify ID to remove");
                logger.warn("remove_key without argument");
                return null;
            }
            logger.debug("Команда remove_key с аргументом: {}", arg);
            return new ClientRequest(cmd, arg);
        }

        // Команда insert: читаем объект организации
        if (cmd.equals("insert")) {
            logger.debug("Начало ввода новой организации");
            try {
                Organization org = readOrganization();
                logger.info("Организация успешно введена: {}", org.getName());
                return new ClientRequest(cmd, org);
            } catch (InputCancelledException e) {
                logger.info("Ввод организации отменён пользователем");
                System.out.println("Input cancelled.");
                return null;
            } catch (Exception e) {
                logger.error("Ошибка при вводе организации: {}", e.getMessage(), e);
                System.err.println("Input error: " + e.getMessage());
                return null;
            }
        }

        // Команда server_save
        if (cmd.equals("server_save")) {
            logger.debug("Команда server_save");
            return new ClientRequest(cmd);
        }

        logger.warn("Неизвестная команда: {}", cmd);
        System.out.println("Unknown command. Type 'help'.");
        return null;
    }

    private Organization readOrganization() {
        Organization org = new Organization();

        System.out.println("=== Enter organization data (type 'cancel' to abort) ===");

        org.setName(InputHelper.readString(scanner, "Name: ", false));

        long x = InputHelper.readLong(scanner, "Coordinate X (> -410): ", -410, null);
        double y = InputHelper.readDouble(scanner, "Coordinate Y: ");
        org.setCoordinates(new models.Coordinates(x, y));

        org.setAnnualTurnover(InputHelper.readLong(scanner, "Annual turnover (> 0): ", 0, null));
        org.setFullName(InputHelper.readString(scanner, "Full name (optional): ", true));

        org.setType(InputHelper.readEnum(scanner, "Organization type (or empty): ", models.OrganizationType.class));

        String street = InputHelper.readString(scanner, "Street address (optional): ", true);
        org.setPostalAddress(new models.Address(street));

        return org;
    }

    public void printHelp() {
        System.out.println("""
            Available commands:
            help                       – show this help
            info                       – display collection info
            show                       – show all elements (sorted by name)
            insert                     – add a new element
            remove_key {id}            – remove element by ID
            clear                      – clear the collection
            server_save                – save collection on server
            history                    – show command history (server-side)
            exit                       – exit client
            Enter 'cancel' at any prompt to abort input.
            """);
        logger.debug("Help printed");
    }
}