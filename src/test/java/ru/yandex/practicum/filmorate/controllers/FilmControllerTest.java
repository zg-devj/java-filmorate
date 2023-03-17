package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private UserController userController;
    private FilmController filmController;
    private User user;
    private Film film;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(new FilmService(filmStorage, userStorage));
        userController = new UserController(new UserService(userStorage));
        film = new Film(1L, "Film", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        user = new User(1L, "user@example.com", "name", "login",
                LocalDate.of(2000, 01, 01));
    }

    @Test
    public void getAllFilms_ReturnEmptyList_GETMethod() {
        assertEquals(0, filmController.allFilms().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void getAllFilms_Return1_GETMethod() {
        Film film1 = new Film(1L, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film1);

        assertEquals(1, filmController.allFilms().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void getFilmById_WithNormalBehavior() {
        Film film1 = new Film(1L, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film1);

        assertEquals(film1, filmController.findFilmById(1L),
                "Разные объекты");
    }

    @Test
    public void createFilm_WithNormalFilm() {
        Film film = new Film(1L, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film);

        assertEquals(1, filmController.allFilms().size());
    }

    @Test
    public void updateFilm_WithNormalBehavior() {
        Film film = new Film(1L, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film);

        Film updatedFilm = new Film(1L, "Film 2", "Desc 2",
                LocalDate.of(2000, 11, 11), 200);
        filmController.updateFilm(updatedFilm);

        assertEquals(1, filmController.allFilms().size());
    }

    @Test
    public void updateFilm_WithWrongId_ReturnException() {
        assertEquals(0, filmController.allFilms().size(),
                "Не верное количество фильмом");
        final String expected = "Фильма с id=" + film.getId() + " не существует";

        doTestUpdateNotFound(film, expected);
    }

    @Test
    public void findPopularFilm_Return1MostPopular() {
        Film film1 = new Film(1L, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        film1.setRate(1L);
        Film film2 = new Film(2L, "Film 2", "Desc",
                LocalDate.of(2021, 01, 01), 120);
        film2.setRate(2L);
        filmController.createFilm(film1);
        filmController.createFilm(film2);

        final List<Film> films = filmController.findPopularFilms(1);

        assertEquals(1, films.size(),
                "Не верное количество фильмом");
        assertEquals(film2, films.get(0));
    }

    @Test
    public void likeFilm_OneLike() {
        Film film1 = filmController.createFilm(film);
        User user1 = userController.createUser(user);

        filmController.likeFilm(film1.getId(), user1.getId());

        assertEquals(1, film1.getRate(), "Не верный рейтинг");
        assertEquals(1, user1.getFilmsLike().size(), "Не верное кол-во понравившихся фильмов");
    }

    @Test
    public void likeFilm_OneLikeAndTwoDislike_ReturnValidationException() {
        Film film1 = filmController.createFilm(film);
        User user1 = userController.createUser(user);

        Long filmId = film1.getId();
        Long userId = user1.getId();

        filmController.likeFilm(filmId, userId);
        filmController.dislikeFilm(filmId, userId);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.dislikeFilm(filmId, userId)
        );

        String expected = "Пользователь уже отменил лайк к фильму.";

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void likeFilm_TwoLikeOneFilm_ReturnValidationException() {
        Film film1 = filmController.createFilm(film);
        User user1 = userController.createUser(user);

        Long filmId = film1.getId();
        Long userId = user1.getId();

        filmController.likeFilm(filmId, userId);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.likeFilm(filmId, userId)
        );

        String expected = "Пользователь уже поставил лайк к фильму.";

        assertEquals(expected, ex.getMessage());
    }

    private void doTestUpdateNotFound(Film film, String expected) {
        final NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(film)
        );

        assertEquals(expected, ex.getMessage());
    }
}