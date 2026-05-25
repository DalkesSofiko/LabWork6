package models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий почтовый адрес организации.
 */
public class Address implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * Улица и номер дома.
     * Поле может принимать значение null.
     */
    private String street;

    /**
     * Конструктор без параметров.
     * Требуется для корректной работы XML-парсера при загрузке данных из файла.
     */
    public Address() {
    }

    /**
     * Конструктор для создания адреса с заданной улицей.
     * @param street название улицы (может быть null)
     */
    public Address(String street) {
        this.street = street;
    }

    /**
     * Возвращает название улицы.
     * @return название улицы или null, если не указано
     */
    public String getStreet() {
        return street;
    }

    /**
     * Устанавливает новое название улицы.
     * @param street новое название улицы (может быть null)
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Строковое представление адреса.
     * Никогда не возвращает null, чтобы избежать NullPointerException при печати.
     */
    @Override
    public String toString() {
        return (street != null) ? street : "[адрес не указан]";
    }

    /**
     * Сравнение адресов по содержимому поля street.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street);
    }

    /**
     * Хеш-код для использования в коллекциях.
     */
    @Override
    public int hashCode() {
        return Objects.hash(street);
    }

}