package server.utils;

import models.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * Утилитный класс для работы с XML-файлами.
 * Простая реализация для лабораторной работы.
 */
public class XMLHandler {

    /**
     * Загружает коллекцию организаций из файла.
     * @param filename путь к файлу
     * @return коллекция организаций
     */
    public static HashMap<Integer, Organization> loadFromFile(String filename) {
        HashMap<Integer, Organization> collection = new HashMap<>();
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            System.out.println("Файл не найден или пуст. Создана новая коллекция.");
            return collection;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(bis);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("organization");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element orgElement = (Element) nodeList.item(i);
                Organization org = parseOrganization(orgElement);
                if (org != null && org.getId() != 0) {
                    collection.put(org.getId(), org);
                }
            }
            System.out.println("Загружено организаций: " + collection.size());

        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
        return collection;
    }

    /**
     * Сохраняет коллекцию в файл.
     * @param filename путь к файлу
     * @param collection коллекция для сохранения
     * @throws IOException если не удалось записать
     */
    public static void saveToFile(String filename, HashMap<Integer, Organization> collection) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("collection");
            document.appendChild(root);

            for (Organization org : collection.values()) {
                Element orgElement = createOrganizationElement(document, org);
                root.appendChild(orgElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            try (FileWriter writer = new FileWriter(filename)) {
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
            }
            System.out.println("Коллекция сохранена: " + filename);

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            throw new IOException("Не удалось сохранить файл", e);
        }
    }

    // === Вспомогательные методы парсинга ===

    private static Organization parseOrganization(Element element) {
        try {
            Organization org = new Organization();
            org.setId(Integer.parseInt(getText(element, "id")));

            String name = getText(element, "name");
            if (name == null || name.isEmpty()) return null;
            org.setName(name);

            org.setCoordinates(parseCoordinates(element));

            String dateStr = getText(element, "creationDate");
            if (dateStr != null) org.setCreationDate(new Date(Long.parseLong(dateStr)));

            String turnStr = getText(element, "annualTurnover");
            if (turnStr != null) org.setAnnualTurnover(Long.parseLong(turnStr));

            org.setFullName(getText(element, "fullName"));

            String typeStr = getText(element, "type");
            if (typeStr != null && !typeStr.isEmpty()) {
                org.setType(OrganizationType.valueOf(typeStr));
            }

            org.setPostalAddress(parseAddress(element));
            return org;
        } catch (Exception e) {
            System.err.println("Ошибка парсинга: " + e.getMessage());
            return null;
        }
    }

    private static Coordinates parseCoordinates(Element parent) {
        try {
            NodeList list = parent.getElementsByTagName("coordinates");
            if (list.getLength() == 0) return null;
            Element el = (Element) list.item(0);
            return new Coordinates(
                    Long.parseLong(getText(el, "x")),
                    Double.parseDouble(getText(el, "y"))
            );
        } catch (Exception e) { return null; }
    }

    private static Address parseAddress(Element parent) {
        try {
            NodeList list = parent.getElementsByTagName("postalAddress");
            if (list.getLength() == 0) return null;
            Element el = (Element) list.item(0);
            return new Address(getText(el, "street"));
        } catch (Exception e) { return null; }
    }

    // === Вспомогательные методы записи ===

    private static Element createOrganizationElement(Document doc, Organization org) {
        Element el = doc.createElement("organization");
        add(doc, el, "id", String.valueOf(org.getId()));
        add(doc, el, "name", org.getName());
        add(doc, el, "creationDate", String.valueOf(org.getCreationDate().getTime()));
        add(doc, el, "annualTurnover", String.valueOf(org.getAnnualTurnover()));
        add(doc, el, "fullName", org.getFullName());

        if (org.getType() != null) add(doc, el, "type", org.getType().name());

        if (org.getCoordinates() != null) {
            Element coords = doc.createElement("coordinates");
            add(doc, coords, "x", String.valueOf(org.getCoordinates().getX()));
            add(doc, coords, "y", String.valueOf(org.getCoordinates().getY()));
            el.appendChild(coords);
        }

        if (org.getPostalAddress() != null) {
            Element addr = doc.createElement("postalAddress");
            add(doc, addr, "street", org.getPostalAddress().getStreet());
            el.appendChild(addr);
        }
        return el;
    }

    private static void add(Document doc, Element parent, String tag, String text) {
        Element el = doc.createElement(tag);
        el.setTextContent(text != null ? text : "");
        parent.appendChild(el);
    }

    private static String getText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0) {
            String t = list.item(0).getTextContent();
            return t != null ? t.trim() : null;
        }
        return null;
    }
}