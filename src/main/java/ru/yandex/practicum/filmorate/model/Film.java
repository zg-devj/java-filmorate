package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.utils.validate.MinBoundDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Фильм
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {
    /**
     * Идентификатор фильма
     */
    private Integer id;
    /**
     * Название фильма
     */
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    /**
     * Описание фильма
     */
    @Size(max = 200, message = "Длина описания не должна быть больше 200 символов.")
    private String description;
    /**
     * Дата релиза
     * Дата релиза не может быть раньше 1895-12-28
     */
    @MinBoundDate(date = "1895-12-28")
    private LocalDate releaseDate;
    /**
     * Продолжительность фильма
     */
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
