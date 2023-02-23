package ru.yandex.practicum.filmorate.exceptions;

// для кода 400
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
