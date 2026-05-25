package models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/**
 * Класс, представляющий организацию.
 * <p>
 * Поля id и creationDate генерируются автоматически.
 */
public class Organization implements Comparable<Organization>, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Компаратор для сортировки по имени
     */
    public static Comparator<Organization> NAME_COMPARATOR =
            Comparator.comparing(Organization::getName, Comparator.nullsLast(Comparator.naturalOrder()));
    /**
     * Уникальный идентификатор организации.
     * Значение должно быть больше 0 и генерируется автоматически.
     */
    private int id;

    /**
     * Название организации.
     * Не может быть null или пустой строкой.
     */
    private String name;

    /**
     * Координаты расположения организации.
     * Не могут быть null.
     */
    private Coordinates coordinates;

    /**
     * Дата и время создания записи об организации.
     * Генерируется автоматически при создании объекта.
     */
    private Date creationDate;

    /**
     * Годовой оборот организации.
     * Значение должно быть строго больше 0.
     */
    private long annualTurnover;

    /**
     * Полное официальное название организации.
     * Не может быть null или пустой строкой.
     */
    private String fullName;

    /**
     * Тип организации (например, государственная, частная и т.д.).
     * Может принимать значение null.
     */
    private OrganizationType type;

    /**
     * Почтовый адрес организации.
     * Не может быть null.
     */
    private Address postalAddress;

    /**
     * Статический счётчик для генерации уникальных ID.
     */
    private static int nextId = 1;

    /**
     * Устанавливает следующее значение ID для генератора.
     * Используется после загрузки коллекции из файла, чтобы избежать дублирования ID.
     *
     * @param nextId следующее свободное значение ID
     */
    public static void setNextId(int nextId) {
        Organization.nextId = nextId;
    }

    /**
     * Конструктор без параметров.
     * Требуется для корректной работы XML-парсера при загрузке данных из файла.
     */
    public Organization() {
    }

    /**
     * Конструктор для создания новой организации (на клиенте).
     * ID и creationDate будут установлены сервером.
     */
    public Organization(String name, Coordinates coordinates, long annualTurnover,
                        String fullName, OrganizationType type, Address postalAddress) {
        // ID и creationDate не устанавливаем — это делает сервер!
        setName(name);
        setCoordinates(coordinates);
        setAnnualTurnover(annualTurnover);
        setFullName(fullName);
        this.type = type;
        setPostalAddress(postalAddress);
    }

    /**
     * Внутренний конструктор для сервера (с генерацией полей).
     * @param nextId следующее свободное ID
     */
    Organization(int nextId) {
        this.id = nextId;
        this.creationDate = new Date();
    }

    // === Геттеры и сеттеры ===

    public int getId() {
        return id;
    }

    /**
     * Устанавливает ID. Вызывается только сервером при добавлении.
     */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть больше 0!");
        }
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым!");
        }
        this.name = name.trim();
    }

    public Coordinates getCoordinates() { return coordinates; }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null!");
        }
        this.coordinates = coordinates;
    }

    public Date getCreationDate() { return creationDate; }

    /**
     * Устанавливает дату создания (только при загрузке из файла).
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getAnnualTurnover() { return annualTurnover; }

    public void setAnnualTurnover(long annualTurnover) {
        if (annualTurnover <= 0) {
            throw new IllegalArgumentException("Годовой оборот должен быть > 0!");
        }
        this.annualTurnover = annualTurnover;
    }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Полное имя не может быть пустым!");
        }
        this.fullName = fullName.trim();
    }

    public OrganizationType getType() { return type; }
    public void setType(OrganizationType type) { this.type = type; }

    public Address getPostalAddress() { return postalAddress; }

    public void setPostalAddress(Address postalAddress) {
        if (postalAddress == null) {
            throw new IllegalArgumentException("Адрес не может быть null!");
        }
        this.postalAddress = postalAddress;
    }

    @Override
    public int compareTo(Organization other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", annualTurnover=" + annualTurnover +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", postalAddress=" + postalAddress +
                '}';
    }
}