package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class ValidateUtil {
    public static void validLongNotNull(Long id, String message) {
        if (id == null) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }

    public static void validUserNotNull(User user, String message) {
        if (user == null) {
            log.debug(message);
            throw new NotFoundException(message);
        }
    }
}
