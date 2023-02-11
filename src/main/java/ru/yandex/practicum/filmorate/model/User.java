package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
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
    private Integer id;
    /**
     * Адрес электронной почты пользователя
     */
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    @Email(message = "Не является адресом электронной почты.")
    private String email;
    /**
     * Логин пользователя
     */
    @NotBlank(message = "Логин не может быть пустым.")
    private String login;
    /**
     * Имя пользователя для отображения
     */
    private String name;
    /**
     * День рождения пользователя
     */
    @Past(message = "День рождения не может быть в будущем.")
    private LocalDate birthday;
}
