package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;


public interface DirectorStorage {
    List<Director> getDirectors();

    List<Director> getDirectorsByFilmId(Long filmId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer directorId);

    boolean isDirectorExists(Integer directorId);

    Optional<Director> getDirectorById(Integer id);
}
