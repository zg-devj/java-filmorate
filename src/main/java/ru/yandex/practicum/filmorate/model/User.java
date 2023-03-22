package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.utils.validators.NotWhiteSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Пользователь
 */
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * Идентификатор пользователя
     */
    private Long id;
    /**
     * Адрес электронной почты пользователя
     */
    @NotEmpty(message = "Адрес электронной почты не может быть пустым.")
    @Email(message = "Не является адресом электронной почты.")
    private String email;
    /**
     * Имя пользователя для отображения
     */
    private String name;
    /**
     * Логин пользователя
     */
    @NotEmpty(message = "Логин не может быть пустым.")
    @NotWhiteSpace(message = "Логин не должен содержать пробелы.")
    private String login;
    /**
     * День рождения пользователя
     */
    @Past(message = "День рождения не может быть в будущем.")
    private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    private Set<Long> friends = new HashSet<>();

    @Setter(AccessLevel.NONE)
    private Set<Long> filmsLike = new HashSet<>();

    public User(Long id, String email, String name, String login, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
    }

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
