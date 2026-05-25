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

    private final Scanner scanner;

    public ClientConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Считывает команду из консоли и формирует запрос для отправки на сервер.
     * @return готовый ClientRequest или null, если введён "exit" или обработана локальная команда (help)
     */
    public ClientRequest readCommand() {
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;

        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        // Локальная команда help – не отправляем на сервер
        if (cmd.equals("help")) {
            printHelp();
            return null;
        }

        // Команда exit – завершение клиента (без отправки)
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
                System.out.println("Ошибка: укажите ID для удаления");
                logger.warn("remove_key без аргумента");
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
                System.out.println("Ввод отменён.");
                return null;
            } catch (Exception e) {
                logger.error("Ошибка при вводе организации: {}", e.getMessage(), e);
                System.err.println("Ошибка ввода: " + e.getMessage());
                return null;
            }
        }

        // Команда server_save (только для сервера)
        if (cmd.equals("server_save")) {
            logger.debug("Команда server_save");
            return new ClientRequest(cmd);
        }

        logger.warn("Неизвестная команда: {}", cmd);
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
            server_save                — сохранить коллекцию на сервере
            history                    — показать историю команд (на сервере)
            exit                       — завершить работу клиента
            Ввод 'отмена' или 'cancel' на любом этапе отменяет текущую команду.
            """);
        logger.debug("Выведена справка по командам");
    }
}