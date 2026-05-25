package server;

import models.Organization;
import shared.ClientRequest;
import shared.ServerResponse;
import java.util.*;
import java.util.stream.Collectors;

public class CommandProcessor {
    private final HashMap<Integer, Organization> collection;
    private int nextId;
    private final CollectionFileHandler fileHandler;

    public CommandProcessor(HashMap<Integer, Organization> collection, int nextId, CollectionFileHandler fileHandler) {
        this.collection = collection;
        this.nextId = nextId;
        this.fileHandler = fileHandler;
    }

    // Доступ к коллекции нужен для сохранения извне
    public HashMap<Integer, Organization> getCollection() { return collection; }

    public ServerResponse process(ClientRequest request) {
        return switch (request.getCommandName()) {
            case "info"        -> handleInfo();
            case "show"        -> handleShow();
            case "insert"      -> handleInsert((Organization) request.getArgument());
            case "remove_key"  -> handleRemoveKey((String) request.getArgument());
            case "server_save" -> handleServerSave();
            default            -> new ServerResponse(false, "Неизвестная команда: " + request.getCommandName());
        };
    }

    private ServerResponse handleInfo() {
        String info = String.format("Тип: HashMap\nРазмер: %d\nДата инициализации: %s\nПустая: %b",
                collection.size(), new Date(), collection.isEmpty());
        return new ServerResponse(true, info);
    }

    private ServerResponse handleShow() {
        if (collection.isEmpty()) return new ServerResponse(true, "Коллекция пуста");

        // Требование: сортировка по имени перед отправкой
        List<Organization> sorted = collection.values().stream()
                .sorted(Comparator.comparing(Organization::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return new ServerResponse(true, "Элементы коллекции (отсортированы по имени):", sorted);
    }

    private ServerResponse handleInsert(Organization org) {
        if (org == null) return new ServerResponse(false, "Объект не может быть null");
        org.setId(nextId++);
        collection.put(org.getId(), org);
        return new ServerResponse(true, "Организация добавлена. ID: " + org.getId());
    }

    private ServerResponse handleRemoveKey(String keyStr) {
        try {
            int id = Integer.parseInt(keyStr);
            return collection.remove(id) != null
                    ? new ServerResponse(true, "Элемент с ID " + id + " удалён")
                    : new ServerResponse(false, "Элемент с таким ID не найден");
        } catch (NumberFormatException e) {
            return new ServerResponse(false, "Некорректный формат ID");
        }
    }

    private ServerResponse handleServerSave() {
        fileHandler.saveCollection(collection);
        return new ServerResponse(true, "Коллекция успешно сохранена на сервере");
    }
}