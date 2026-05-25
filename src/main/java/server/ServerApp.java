package server;

import models.Organization;
import java.util.HashMap;

public class ServerApp {
    public static void main(String[] args) {
        String filePath = args.length > 0 ? args[0] : "data.xml";
        int port = 5000;

        // Загрузка коллекции
        CollectionFileHandler fileHandler = new CollectionFileHandler(filePath);
        HashMap<Integer, Organization> collection = fileHandler.loadCollection();
        int nextId = collection.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;

        // Инициализация обработчика
        CommandProcessor processor = new CommandProcessor(collection, nextId, fileHandler);

        // Автосохранение при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Завершение работы сервера. Сохранение коллекции...");
            fileHandler.saveCollection(collection);
        }));

        // 4. Запуск сети
        try {
            ServerNetwork network = new ServerNetwork(port, processor);
            network.start();
        } catch (Exception e) {
            System.err.println("Критическая ошибка сервера: " + e.getMessage());
        }
    }
}