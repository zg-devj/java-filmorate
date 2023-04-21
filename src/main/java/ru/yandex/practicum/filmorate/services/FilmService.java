package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.FilmDirectorDto;
import ru.yandex.practicum.filmorate.model.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.*;
import java.util.stream.Collectors;

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
    private final FilmDirectorStorage filmDirectorStorage;
    private final GenreStorage genreStorage;
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    // вернуть все фильмы
    public List<Film> findAllFilms() {
        List<Film> allFilms = filmStorage.findAllFilms();
        log.info("Запрошены все фильмы в количестве {}.", allFilms.size());
        return allFilms;
    }

    // вернуть фильм по id
    public Film findFilmById(Long id) {
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
    public List<Film> findPopularFilms(Integer genreId, Integer year, int count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть <=0");
        }
        List<Film> allFilms = filmStorage.findPopularFilms(genreId, year, count);
        log.info("Запрошены {} популярных фильмов. Возвращено {}. genreId={}, release_date={}",
                count, allFilms.size(), genreId, year);

        List<Long> idS = allFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        List<FilmGenreDto> filmGenreList = filmGenreDbStorage.findFilmGenreAll(idS);
        List<FilmDirectorDto> filmDirectorList = filmDirectorStorage.findFilmDirectorAll(idS);

        for (Film film : allFilms) {
            film.setGenres(
                    filmGenreList.stream().filter(f -> Objects.equals(film.getId(), f.getFilmId()))
                            .map(o -> Genre.builder()
                                    .id(o.getGenreId())
                                    .name(o.getGenreName())
                                    .build())
                            .collect(Collectors.toList())
            );
            film.setDirectors(
                    filmDirectorList.stream().filter(f -> Objects.equals(film.getId(), f.getFilmId()))
                            .map(o -> Director.builder()
                                    .id(o.getDirectorId())
                                    .name(o.getDirectorName())
                                    .build())
                            .collect(Collectors.toList())
            );
        }
        return allFilms;
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
        checkFilm(id);
        checkUser(userId);
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
        checkFilm(id);
        checkUser(userId);
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
        checkUser(userId);
        return filmStorage.getRecommendations(userId);
    }


    public List<Film> sharedUserMovies(Long userId, Long friendId) { // получение общих фильмов пользователей
        checkUser(userId);
        checkUser(friendId);
        return filmStorage.sharedUserMovies(userId, friendId);
    }

    public List<Film> searchForMoviesByDescription(String query, String by) {
        log.info("Запрошен фильм по ключевым словам: {}", query);
        return filmStorage.searchForMoviesByDescription(query, by);
    }

    private void checkFilm(Long filmId) {
        ValidateUtil.validNumberNotNull(filmId, "id фильма не должно быть null.");
        if (!filmStorage.checkFilm(filmId)) {
            ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", filmId));
        }
    }

    private void checkUser(Long userId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
    }

    public void removeFilmById(Long id) {
        if (!filmStorage.checkFilm(id)) {
            ValidateUtil.throwNotFound(String.format("Фильм с id=%d не найден.", id));
        }

        //Удалить жанры фильма
        genreStorage.deleteFilmGenresByFilmId(id);

        //удалить лайки фильма
        filmStorage.deleteLikesByFilmId(id);

        //удалить ревью
        reviewStorage.deleteAllReviewByFilmId(id);

        //удалить информацию о режиссерах
        filmDirectorStorage.deleteRecords(id);

        //Удалить фильм
        filmStorage.deleteFilm(id);
    }
}
