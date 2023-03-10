package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.utils.validators.MinBoundaryDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Фильм
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Film {
    /**
     * Идентификатор фильма
     */
    private Long id;
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
    @MinBoundaryDate(date = "1895-12-28")
    private LocalDate releaseDate;
    /**
     * Продолжительность фильма
     */
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;

    private Long rate = Long.valueOf(0);

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Long rate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }

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
