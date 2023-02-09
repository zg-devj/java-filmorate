package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    private String name;
    /**
     * Описание фильма
     */
    private String description;
    /**
     * Дата релиза
     */
    private LocalDate releaseDate;
    /**
     * Продолжительность фильма
     */
    private int duration;
}
