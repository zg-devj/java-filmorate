package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Slf4j
public class ValidateUtil {
    public static void validLongNotNull(Long id, String message) {
        if (id == null) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }

    public static Object throwNotFound(String message) {
        log.debug(message);
        return new NotFoundException(message);
    }
}
