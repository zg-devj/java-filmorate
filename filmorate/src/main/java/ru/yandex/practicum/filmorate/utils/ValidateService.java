package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

@Slf4j
public class ValidateService {
    public static void isEmptyStringField(String param, String message) {
        if (param == null || param.isBlank()) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    public static void checkMaxSizeStringField(String param, int maxSize, String message) {
        if (param.length() > maxSize) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    public static void dateLessThen(LocalDate param, LocalDate lessThen, String message) {
        if (param.isBefore(lessThen)) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    public static void durationMoreThenZero(int param, String message) {
        if (param <= 0) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    public static void isEmpty(int count, String message) {
        if (count <= 0) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    public static void containsFilm(boolean contains, String message) {
        if (contains) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }
}
