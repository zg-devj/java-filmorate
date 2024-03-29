package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.dto.FilmGenreDto;

import java.util.List;

public interface FilmGenreStorage {
    void create(Long filmId, List<Genre> genres);

    void deleteGenresByFilmId(Long filmId);

    List<FilmGenreDto> findFilmGenreAll(List<Long> filmsIds);
}
