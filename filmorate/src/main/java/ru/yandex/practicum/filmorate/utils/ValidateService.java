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
    public static void durationMoreThenZero(int param, String message) {
        if (param <= 0) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    /**
     * Пустой ли список
     *
     * @param count   количество элементов в списке
     * @param message текст ошибки
     */
    public static void isEmptyList(int count, String message) {
        if (count <= 0) {
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
}
