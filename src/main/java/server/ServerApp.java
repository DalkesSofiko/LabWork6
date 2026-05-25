package server;

import models.Organization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class ServerApp {

    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {

        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

        // Парсинг аргументов: [путь_к_файлу] [порт]
        String filePath = (args.length > 0 && !args[0].isEmpty()) ? args[0] : "data.xml";
        int port = 5000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.warn("Некорректный порт, используется значение по умолчанию: {}", port);
            }
        }
        logger.info("Запуск сервера с параметрами: файл = {}, порт = {}", filePath, port);

        // Загрузка коллекции
        CollectionFileHandler fileHandler = new CollectionFileHandler(filePath);
        HashMap<Integer, Organization> collection = fileHandler.loadCollection();
        int nextId = collection.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
        logger.info("Коллекция загружена. Элементов: {}, следующий ID: {}", collection.size(), nextId);

        // Инициализация обработчика
        CommandProcessor processor = new CommandProcessor(collection, nextId, fileHandler);

        // Хук для автосохранения при завершении (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Получен сигнал завершения. Сохранение коллекции...");
            fileHandler.saveCollection(collection);
            logger.info("Коллекция сохранена. Сервер останавливается.");
        }));

        // Запуск сетевого модуля
        try {
            ServerNetwork network = new ServerNetwork(port, processor);
            network.start();
        } catch (Exception e) {
            logger.error("Критическая ошибка сервера: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}