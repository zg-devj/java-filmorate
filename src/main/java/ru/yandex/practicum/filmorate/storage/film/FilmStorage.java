package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.FilmRateDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAllFilms();

    Collection<FilmRateDto> findPopularFilms(int limit);

    Optional<Film> findFilmById(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Boolean checkFilm(Long filmId);
}
