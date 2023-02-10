package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Фильм
 */
@Data
@AllArgsConstructor
public class Film {
    /**
     * Идентификатор фильма
     */
    private int id;
    /**
     * Название фильма
     */
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    /**
     * Описание фильма
     */
    @Size(max = 200,message = "Длина описания не должна быть больше 200 символов.")
    private String description;
    /**
     * Дата релиза
     */
    private LocalDate releaseDate;
    /**
     * Продолжительность фильма
     */
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}
