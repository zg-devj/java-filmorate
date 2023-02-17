package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

/**
 * Сервис проверки значений
 */
@Slf4j
public class ValidateService {
    /**
     * Строка равна null или пустая
     *
     * @param param   проверяемое значение
     * @param message текст ошибки
     */
    public static void isEmptyStringField(String param, String message) {
        if (param == null || param.isBlank()) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Проверить на максимальное количество символов
     *
     * @param param   сравниваемое значение
     * @param maxSize максимальное число символов
     * @param message текст ошибки
     */
    public static void checkMaxSizeStringField(String param, int maxSize, String message) {
        if (param.length() > maxSize) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Дата раньше установленной
     *
     * @param param    проверяемая дата
     * @param lessThen сравниваемая (устанавливаемая) дата
     * @param message  текст ошибки
     */
    public static void dateLessThen(LocalDate param, LocalDate lessThen, String message) {
        if (param.isBefore(lessThen)) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Продолжительность больше чем ноль
     *
     * @param param   проверяемое значение
     * @param message текст ошибки
     */
    public static void durationMoreThenZero(Integer param, String message) {
        if (param == null || param <= 0) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Существует ли фильм
     *
     * @param contains true - фильм не существует
     * @param message  текст ошибки
     */
    public static void containsFilm(boolean contains, String message) {
        if (contains) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Является ли объект email
     *
     * @param param   объект проверки
     * @param message текст ошибки
     */
    public static void isNotEmail(String param, String message) {
        if (param == null || param.isBlank() || !param.contains("@") || (param.indexOf("@") != param.lastIndexOf("@"))) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Является ли проверяемая дата поз текущего времени
     *
     * @param param   проверяемая дата
     * @param message текст ошибки
     */
    public static void dateLaterThenNow(LocalDate param, String message) {
        if (param.isAfter(LocalDate.now())) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * если param проверяемый параметр равен null или пустое значение,
     * то заменить его на toParam
     *
     * @param param   проверяемый параметр
     * @param toParam замещающее значение
     * @return String param или toParam
     */
    public static String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }

    /**
     * Содержит ли строка пробелы
     * @param param проверяемый параметр
     * @param message текст ошибки
     */
    public static void isStringFieldWhiteSpace(String param, String message) {
        if (param.chars().anyMatch(Character::isWhitespace)) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }
}
