package ru.yandex.practicum.filmorate.storage.filmganre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmGenreStorage {
    void create(Long filmId, List<Genre> genres);
    void deleteGenresByFilmId(Long filmId);
}
