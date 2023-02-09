package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

public class ValidateService {
    public static void isEmptyStringField(String param, String message) throws ValidationException {
        if (param == null || param.isBlank()) {
            throw new ValidationException(message);
        }
    }

    public static void checkMaxSizeStringField(String param, int maxSize, String message) throws ValidationException {
        if (param.length() > maxSize) {
            throw new ValidationException(message);
        }
    }

    public static void dateLessThen(LocalDate param, LocalDate lessThen, String message) throws ValidationException {
        if (param.isBefore(lessThen)) {
            throw new ValidationException(message);
        }
    }

    public static void durationMoreThenZero(int param, String message) throws ValidationException {
        if (param <= 0) {
            throw new ValidationException(message);
        }
    }
}
