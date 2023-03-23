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
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;

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
    public List<Film> findPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть <=0");
        }
        log.debug("Запрошены {} популярных фильмов.", count);
//        return filmStorage.findAllFilms().stream()
//                .sorted((o1, o2) -> o2.getRate().compareTo(o1.getRate())).limit(count)
//                .collect(Collectors.toList());
        return new ArrayList<>();
    }

    // пользователь ставит лайк фильму
    public void likeFilm(Long id, Long userId) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");

        Film film = filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
                    return null;
                }
        );

        User user = userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );


//        if(!user.getFilmsLike().contains(id)) {
//            // если пользователь не ставил лайк
//            film.setRate(film.getRate() + 1);
//            user.getFilmsLike().add(id);
//            log.debug("Пользователь с id={} поставил лайк фильму с id={}.", userId, id);
//        } else{
//            log.debug("Пользователь с id={} уже ставил лайк фильму с id={}.", userId, id);
//            throw new ValidationException("Пользователь уже поставил лайк к фильму.");
//        }
    }

    // пользователь удаляет лайк.
    public void dislikeFilm(Long id, Long userId) {
        ValidateUtil.validLongNotNull(id, "id фильма не должно быть null.");
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");

        Film film = filmStorage.findFilmById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", id));
                    return null;
                }
        );
        User user = userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );

//        if(user.getFilmsLike().contains(id)) {
//            // Если пользователь ставил лайк
//            film.setRate(film.getRate() - 1);
//            user.getFilmsLike().remove(id);
//            log.debug("Пользователь с id={} отменил лайк фильму с id={}.", userId, id);
//        } else{
//            log.debug("У пользователя с id={} нет лайка к фильму с id={}. Нельзя отменить лайк.", userId, id);
//            throw new ValidationException("Пользователь уже отменил лайк к фильму.");
//        }
    }
}
