package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    // вернуть все фильмы
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    // вернуть фильм по id
    public Film findFilmById(Long id) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        Film film = filmStorage.findFilmById(id);
        ValidateUtil.validFilmNotNull(film, String.format("Фильма с id=%d не существует.", id));
        return film;
    }

    // добавить фильм
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    // обновить фильм
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }
}
