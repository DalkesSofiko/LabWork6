package models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Типы организаций.
 * Реализует Serializable для передачи по сети.
 */
public enum OrganizationType implements Serializable {

    PUBLIC,
    GOVERNMENT,
    TRUST,
    PRIVATE_LIMITED_COMPANY;

    private static final long serialVersionUID = 1L;

    /**
     * Преобразование строки в константу перечисления.
     * @param value строка для преобразования
     * @return константу или null, если преобразование невозможно
     */
    public static OrganizationType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return OrganizationType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Возвращает список допустимых значений для подсказки пользователю.
     * @return строка со списком констант через запятую
     */
    public static String getAvailableValues() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}