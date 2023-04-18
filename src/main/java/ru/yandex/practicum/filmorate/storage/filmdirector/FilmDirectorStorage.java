package ru.yandex.practicum.filmorate.storage.filmdirector;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.FilmDirectorDto;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorStorage {

    Collection<Director> getFilmDirectors(Long filmId);

    List<FilmDirectorDto> findFilmDirectorAll(List<Long> filmsIds);
}
