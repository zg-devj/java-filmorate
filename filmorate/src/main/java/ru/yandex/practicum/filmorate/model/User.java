package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Пользователь
 */
@Data
public class User {
    /**
     * Идентификатор пользователя
     */
    private final int id;
    /**
     * Адрес электронной почты пользователя
     */
    private String email;
    /**
     * Имя пользователя
     */
    private String name;
    /**
     * День рождения пользователя
     */
    private LocalDate birthday;
}
