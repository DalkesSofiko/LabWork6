package models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий координаты расположения организации.
 */
public class Coordinates implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * Координата X.
     * Значение должно быть строго больше -410.
     */
    private long x;

    /**
     * Координата Y.
     * Не имеет ограничений по значению.
     */
    private double y;

    /**
     * Конструктор без параметров.
     * Требуется для корректной работы XML-парсера при загрузке данных из файла.
     */
    public Coordinates() {
    }

    /**
     * Конструктор для создания координат с заданными значениями.
     * Проверяет валидность координаты X.
     *
     * @param x координата X (должна быть > -410)
     * @param y координата Y
     * @throws IllegalArgumentException если x меньше или равен -410
     */
    public Coordinates(long x, double y) {
        setX(x);
        this.y = y;
    }

    /**
     * Внутренний конструктор без валидации (для парсинга старых данных).
     * Используется только внутри XMLHandler при загрузке.
     */
    Coordinates(long x, double y, boolean skipValidation) {
        this.x = x;
        this.y = y;
    }

    /**
     * Устанавливает новое значение координаты X.
     * Проверяет, что значение строго больше -410.
     *
     * @param x новое значение координаты X
     * @throws IllegalArgumentException если x меньше или равен -410
     */
    public void setX(long x) {
        if (x <= -410) {
            throw new IllegalArgumentException("Координата X должна быть больше -410!");
        }
        this.x = x;
    }

    /**
     * Возвращает значение координаты X.
     *
     * @return координата X
     */
    public long getX() {
        return x;
    }

    /**
     * Устанавливает новое значение координаты Y.
     *
     * @param y новое значение координаты Y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Возвращает значение координаты Y.
     *
     * @return координата Y
     */
    public double getY() {
        return y;
    }

    /**
     * Возвращает строковое представление координат.
     * Формат: "Координаты организации: (x; y)"
     *
     * @return строка с координатами
     */
    @Override
    public String toString() {
        return "Координаты организации: (" + x + "; " + y + ")";
    }

    /**
     * Сравнение координат по значениям полей.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && Double.compare(that.y, y) == 0;
    }

    /**
     * Хеш-код для использования в коллекциях.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
