package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.FilmDirectorDto;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorStorage {

    Collection<Director> getFilmDirectors(Long filmId);

    void addRecord(Integer directorId, Long filmId);

    void addRecords(List<Director> directors, Long filmId);

    void deleteRecords(Long filmId);

    List<FilmDirectorDto> findFilmDirectorAll(List<Long> filmsIds);
}
