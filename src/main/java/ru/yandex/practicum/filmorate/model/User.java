package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Пользователь
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * Идентификатор пользователя
     */
    private Integer id;
    /**
     * Адрес электронной почты пользователя
     */
    @NotEmpty(message = "Адрес электронной почты не может быть пустым.")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
