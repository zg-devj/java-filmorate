package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final FilmLikeStorage likeStorage;
    private final FilmGenreStorage filmGenreDbStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    // вернуть все фильмы
    public List<Film> findAllFilms() {
        List<Film> allFilms = filmStorage.findAllFilms();
        log.info("Запрошены все фильмы в количестве {}.", allFilms.size());
        return allFilms;
    }

    // вернуть фильм по id
    public Film findFilmById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id фильма не должно быть null.");
        Film film = filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильма с id=%d не существует.", id));
                    return null;
                }
        );
        log.info("Запрошен фильм c id={}.", id);
        return film;
    }

    // добавить фильм
    public Film createFilm(Film film) {
        Mpa mpa = mpaStorage.findMpaById(film.getMpa().getId()).orElseThrow(
                () -> new NotFoundException(String.format("Рейтинга с id={} не существует", film.getMpa().getId()))
        );
        film.setMpa(mpa);
        Film created = filmStorage.createFilm(film);
        log.info("Фильм с id={} добавлен.", created.getId());
        return created;
    }

    // обновить фильм
    public Film updateFilm(Film film) {
        Film updated = filmStorage.updateFilm(film);
        log.info("Фильм с id={} обновлен.", updated.getId());
        return updated;
    }

    // вернуть популярные фильмы
    public List<Film> findPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть <=0");
        }
        log.info("Запрошены {} популярных фильмов.", count);
        return filmStorage.findPopularFilms(count);
    }

    public Collection<Film> getAllFilmsByDirectorSorted(Integer directorId, String sortBy) {
        if (!directorStorage.isDirectorExists(directorId)) {
            throw new NotFoundException("Режиссёр не найден");
        }
        if (sortBy == null || !(sortBy.contentEquals("year") || sortBy.contentEquals("likes"))) {
            throw new NotFoundException("Недопустимый признак для сортировки");
        }
        return filmStorage.getAllFilmsSorted(directorId, sortBy);
    }

    // пользователь ставит лайк фильму
    public void likeFilm(Long id, Long userId) {
        ValidateUtil.validNumberNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!filmStorage.checkFilm(id)) {
            ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
        }
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (likeStorage.create(userId, id)) {
            // пользователь ставит лайк
            log.info("Пользователь с id={} поставил лайк фильму с id={}.", userId, id);
            eventStorage.addEvent(userId, id, EventStorage.TypeName.LIKE, EventStorage.OperationName.ADD);
        } else {
            log.info("Пользователь с id={} уже ставил лайк фильму с id={}.", userId, id);
            throw new ValidationException("Пользователь уже поставил лайк к фильму.");
        }
    }

    // пользователь удаляет лайк
    public void dislikeFilm(Long id, Long userId) {
        ValidateUtil.validNumberNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!filmStorage.checkFilm(id)) {
            ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
        }
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (likeStorage.delete(userId, id)) {
            // пользователь удаляет лайк
            log.info("Пользователь с id={} отменил лайк фильму с id={}.", userId, id);
            eventStorage.addEvent(userId, id, EventStorage.TypeName.LIKE, EventStorage.OperationName.REMOVE);
        } else {
            log.info("У пользователя с id={} нет лайка к фильму с id={}. Нельзя отменить лайк.", userId, id);
            throw new ValidationException("Пользователь уже отменил лайк к фильму.");
        }
    }


    public Collection<Film> getRecommendations(Long userId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        return filmStorage.getRecommendations(userId);
    }


    public List<Film> sharedUserMovies(Long userId, Long friendId) { // получение общих фильмов пользователей
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validNumberNotNull(friendId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (!userStorage.checkUser(friendId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", friendId));
        }
        return filmStorage.sharedUserMovies(userId, friendId);
    }

    public List<Film> searchForMoviesByDescription(String query, String by) {
        log.info("Запрошен фильм по ключевым словам: {}", query);
        return filmStorage.searchForMoviesByDescription(query, by);
    }
}
