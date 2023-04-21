package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> findAllGenres();

    Optional<Genre> findGenreById(Integer genreId);

    List<Genre> findGenresByFilmId(Long filmId);

    void deleteFilmGenresByFilmId(Long id);
}
