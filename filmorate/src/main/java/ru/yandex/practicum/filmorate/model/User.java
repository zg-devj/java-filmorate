package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Пользователь
 */
@Data
@AllArgsConstructor
public class User {
    /**
     * Идентификатор пользователя
     */
    private int id;
    /**
     * Адрес электронной почты пользователя
     */
    private String email;
    /**
     * Логин пользователя
     */
    private String login;
    /**
     * Имя пользователя для отображения
     */
    private String name;
    /**
     * День рождения пользователя
     */
    private LocalDate birthday;
}
