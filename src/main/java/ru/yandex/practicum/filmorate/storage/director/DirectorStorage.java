package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;


public interface DirectorStorage {
    List<Director> getDirectors();

    List<Director> getDirectorsById(Long directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer directorId);

    boolean isDirectorExists(Integer directorId);

    Director getDirectorById(Integer id);
}
