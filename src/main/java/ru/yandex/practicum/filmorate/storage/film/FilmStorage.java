package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAllFilms();

    Collection<Film> findPopularFilms(int limit);

    Optional<Film> findFilmById(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void increaseFilmRate(Long filmId);

    void decreaseFilmRate(Long filmId);

    Boolean checkFilm(Long filmId);
}
