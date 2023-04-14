package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilms();

    List<Film> findPopularFilms(int limit);

    Optional<Film> findFilmById(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Boolean checkFilm(Long filmId);

    List<Film> sharedUserMovies (Long userId, Long friendId);
}
