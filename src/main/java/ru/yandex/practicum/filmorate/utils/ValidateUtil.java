package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Slf4j
public class ValidateUtil {
    public static void validNumberNotNull(Number id, String message) {
        if (id == null) {
            log.info(message);
            throw new ValidationException(message);
        }
    }

    public static void throwNotFound(String message) {
        log.info(message);
        throw new NotFoundException(message);
    }
}
