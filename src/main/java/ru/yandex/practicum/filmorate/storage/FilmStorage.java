package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAllFilms();
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilm(int id);
}
