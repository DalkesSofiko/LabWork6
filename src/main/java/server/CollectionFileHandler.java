package server;

import models.Organization;
import server.utils.XMLHandler; // Твой существующий обработчик
import java.io.File;
import java.util.HashMap;

public class CollectionFileHandler {
    private final String filePath;
    private final XMLHandler xmlHandler;

    public CollectionFileHandler(String filePath) {
        this.filePath = filePath;
        this.xmlHandler = new XMLHandler();
    }

    public HashMap<Integer, Organization> loadCollection() {
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("Файл не найден. Создана пустая коллекция.");
            return new HashMap<>();
        }
        return xmlHandler.loadFromFile(filePath);
    }

    public void saveCollection(HashMap<Integer, Organization> collection) {
        try {
            xmlHandler.saveToFile(filePath, collection);
            System.out.println("Коллекция сохранена в " + filePath);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }
}