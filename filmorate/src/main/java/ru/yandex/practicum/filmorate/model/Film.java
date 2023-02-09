package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Фильм
 */
@Data
public class Film {
    /**
     * Идентификатор фильма
     */
    private final int id;
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
