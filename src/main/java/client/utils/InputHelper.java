package client.utils;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Утилитарный класс для ввода и валидации данных на клиенте.
 */
public class InputHelper {

    /**
     * Локальное исключение для отмены ввода.
     * Не зависит от других пакетов, чтобы клиент был автономным.
     */
    public static class InputCancelledException extends RuntimeException {
        public InputCancelledException() {
            super("Ввод отменён пользователем");
        }
    }

    /**
     * Читает строку с проверкой на пустоту.
     * @param sc сканер
     * @param prompt приглашение к вводу
     * @param nullable можно ли оставить пустым (вернуть null)
     * @return введённая строка или null
     */
    public static String readString(Scanner sc, String prompt, boolean nullable) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("отмена")) {
                throw new InputCancelledException();
            }

            if (input.isEmpty() && nullable) return null;
            if (!input.isEmpty()) return input;
            System.out.println("Поле не может быть пустым.");
        }
    }

    /**
     * Читает long с проверкой границ.
     */
    public static long readLong(Scanner sc, String prompt, long minExclusive, Long maxInclusive) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("отмена")) {
                throw new InputCancelledException();
            }

            if (input.isEmpty()) {
                System.out.println("Ввод не может быть пустым.");
                continue;
            }

            try {
                long val = Long.parseLong(input);

                if (val <= minExclusive) {
                    System.out.println("Значение должно быть > " + minExclusive);
                    continue;
                }
                if (maxInclusive != null && val > maxInclusive) {
                    System.out.println("Значение должно быть <= " + maxInclusive);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ожидается целое число.");
            }
        }
    }

    /**
     * Читает double.
     */
    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("отмена")) {
                throw new InputCancelledException();
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ожидается число.");
            }
        }
    }

    /**
     * Читает enum или null.
     */
    public static <T extends Enum<T>> T readEnum(Scanner sc, String prompt, Class<T> cls) {
        System.out.println("Доступные значения: " + Arrays.toString(cls.getEnumConstants()));
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("отмена")) {
                throw new InputCancelledException();
            }

            if (input.isEmpty()) return null;
            try {
                return Enum.valueOf(cls, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректное значение. Доступно: " + Arrays.toString(cls.getEnumConstants()));
            }
        }
    }
}