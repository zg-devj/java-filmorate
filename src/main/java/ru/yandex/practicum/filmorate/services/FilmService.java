package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final FilmLikeStorage likeStorage;

    // вернуть все фильмы
    public Collection<Film> findAllFilms() {
        Collection<Film> allFilms = filmStorage.findAllFilms();
        log.debug("Запрошены все фильмы в количестве {}.", allFilms.size());
        return allFilms;
    }

    // вернуть фильм по id
    public Film findFilmById(Long id) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        Film film = filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильма с id=%d не существует.", id));
                    return null;
                }
        );
        log.debug("Запрошен фильм c id={}.", id);
        return film;
    }

    // добавить фильм
    public Film createFilm(Film film) {
        Mpa mpa = mpaStorage.findMpaById(film.getMpa().getId()).orElseThrow(
                () -> new NotFoundException(String.format("MPA рейтинга с id={}", film.getMpa().getId()))
        );
        film.setMpa(mpa);
        return filmStorage.createFilm(film);
    }

    // обновить фильм
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    // вернуть популярные фильмы
    public Collection<Film> findPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть <=0");
        }
        log.debug("Запрошены {} популярных фильмов.", count);
        return filmStorage.findPopularFilms(count);
    }

    // пользователь ставит лайк фильму
    public void likeFilm(Long id, Long userId) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");

        filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
                    return null;
                }
        );

        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );

        if (likeStorage.create(userId, id)) {
            // пользователь ставит лайк
            filmStorage.increaseFilmRate(id);
            log.debug("Пользователь с id={} поставил лайк фильму с id={}.", userId, id);
        } else {
            log.debug("Пользователь с id={} уже ставил лайк фильму с id={}.", userId, id);
            throw new ValidationException("Пользователь уже поставил лайк к фильму.");
        }
    }

    // пользователь удаляет лайк.
    public void dislikeFilm(Long id, Long userId) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");

        filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
                    return null;
                }
        );
        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );

        if (likeStorage.delete(userId, id)) {
            // пользователь удаляет лайк
            filmStorage.decreaseFilmRate(id);
            log.debug("Пользователь с id={} отменил лайк фильму с id={}.", userId, id);
        } else {
            log.debug("У пользователя с id={} нет лайка к фильму с id={}. Нельзя отменить лайк.", userId, id);
            throw new ValidationException("Пользователь уже отменил лайк к фильму.");
        }
    }
}
