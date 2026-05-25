package server;

import models.Organization;
import shared.ClientRequest;
import shared.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CommandProcessor {
    private static final Logger logger = LogManager.getLogger(CommandProcessor.class);

    private final HashMap<Integer, Organization> collection;
    private int nextId;
    private final CollectionFileHandler fileHandler;

    public CommandProcessor(HashMap<Integer, Organization> collection, int nextId, CollectionFileHandler fileHandler) {
        this.collection = collection;
        this.nextId = nextId;
        this.fileHandler = fileHandler;
        logger.debug("CommandProcessor инициализирован. nextId = {}", nextId);
    }

    public HashMap<Integer, Organization> getCollection() {
        return collection;
    }

    public ServerResponse process(ClientRequest request) {
        String command = request.getCommandName();
        logger.info("Обработка команды: {}", command);
        try {
            return switch (command) {
                case "info" -> handleInfo();
                case "show" -> handleShow();
                case "insert" -> {
                    Organization org = (Organization) request.getArgument();
                    logger.debug("Параметры insert: name = {}", org != null ? org.getName() : "null");
                    yield handleInsert(org);
                }
                case "remove_key" -> {
                    String key = (String) request.getArgument();
                    logger.debug("Параметры remove_key: id = {}", key);
                    yield handleRemoveKey(key);
                }
                case "server_save" -> handleServerSave();
                default -> {
                    logger.warn("Неизвестная команда: {}", command);
                    yield new ServerResponse(false, "Неизвестная команда: " + command);
                }
            };
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды '{}': {}", command, e.getMessage(), e);
            return new ServerResponse(false, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private ServerResponse handleInfo() {
        String info = String.format("Тип: HashMap\nРазмер: %d\nДата инициализации: %s\nПустая: %b",
                collection.size(), new Date(), collection.isEmpty());
        logger.debug("Информация о коллекции запрошена: размер = {}", collection.size());
        return new ServerResponse(true, info);
    }

    private ServerResponse handleShow() {
        if (collection.isEmpty()) {
            logger.debug("Команда show: коллекция пуста");
            return new ServerResponse(true, "Коллекция пуста");
        }

        // Требование: сортировка по имени перед отправкой
        List<Organization> sorted = collection.values().stream()
                .sorted(Comparator.comparing(Organization::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        logger.info("Команда show: отправлено {} элементов (отсортировано по имени)", sorted.size());
        return new ServerResponse(true, "Элементы коллекции (отсортированы по имени):", sorted);
    }

    private ServerResponse handleInsert(Organization org) {
        if (org == null) {
            logger.warn("Попытка вставки null объекта");
            return new ServerResponse(false, "Объект не может быть null");
        }
        org.setId(nextId++);
        collection.put(org.getId(), org);
        logger.info("Добавлена организация: id = {}, name = '{}'", org.getId(), org.getName());
        return new ServerResponse(true, "Организация добавлена. ID: " + org.getId());
    }

    private ServerResponse handleRemoveKey(String keyStr) {
        if (keyStr == null || keyStr.trim().isEmpty()) {
            logger.warn("remove_key: пустой аргумент");
            return new ServerResponse(false, "Не указан ID для удаления");
        }
        try {
            int id = Integer.parseInt(keyStr.trim());
            Organization removed = collection.remove(id);
            if (removed != null) {
                logger.info("Удалена организация с id = {}, name = '{}'", id, removed.getName());
                return new ServerResponse(true, "Элемент с ID " + id + " удалён");
            } else {
                logger.warn("Попытка удалить несуществующий id = {}", id);
                return new ServerResponse(false, "Элемент с таким ID не найден");
            }
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат ID: '{}'", keyStr);
            return new ServerResponse(false, "Некорректный формат ID");
        }
    }

    private ServerResponse handleServerSave() {
        logger.info("Выполнение server_save");
        try {
            fileHandler.saveCollection(collection);
            logger.info("Коллекция успешно сохранена");
            return new ServerResponse(true, "Коллекция успешно сохранена на сервере");
        } catch (Exception e) {
            logger.error("Ошибка при сохранении коллекции: {}", e.getMessage(), e);
            return new ServerResponse(false, "Ошибка сохранения: " + e.getMessage());
        }
    }
}