package server;

import models.Organization;
import server.utils.XMLHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class CollectionFileHandler {
    private static final Logger logger = LogManager.getLogger(CollectionFileHandler.class);

    private final String filePath;
    private final XMLHandler xmlHandler;

    public CollectionFileHandler(String filePath) {
        this.filePath = filePath;
        this.xmlHandler = new XMLHandler();
        logger.debug("CollectionFileHandler инициализирован с путём: {}", filePath);
    }

    public HashMap<Integer, Organization> loadCollection() {
        logger.info("Загрузка коллекции из файла: {}", filePath);
        File f = new File(filePath);
        if (!f.exists()) {
            logger.warn("Файл не найден: {}. Будет создана пустая коллекция.", filePath);
            return new HashMap<>();
        }
        try {
            HashMap<Integer, Organization> collection = xmlHandler.loadFromFile(filePath);
            logger.info("Коллекция загружена. Количество элементов: {}", collection.size());
            return collection;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке коллекции из файла: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    public void saveCollection(HashMap<Integer, Organization> collection) {
        logger.info("Сохранение коллекции в файл: {}", filePath);
        try {
            xmlHandler.saveToFile(filePath, collection);
            logger.info("Коллекция успешно сохранена. Количество элементов: {}", collection.size());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении коллекции в файл: {}", e.getMessage(), e);
        }
    }
}