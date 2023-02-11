package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film(1, "Film", "Desc",
                LocalDate.of(2022, 01, 01), 100);
    }

    @Test
    public void getAllFilms_ReturnEmptyList_GETMethod() {
        assertEquals(0, filmController.allFilms().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void getAllFilms_Return1_GETMethod() {
        Film film1 = new Film(1, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film1);

        assertEquals(1, filmController.allFilms().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void createFilm_WithNormalFilm() {
        Film film = new Film(1, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film);

        assertEquals(1, filmController.allFilms().size());
    }

    @Test
    public void updateFilm_WithNormalBehavior() {
        Film film = new Film(1, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        filmController.createFilm(film);

        Film updatedFilm = new Film(1, "Film 2", "Desc 2",
                LocalDate.of(2000, 11, 11), 200);
        filmController.updateFilm(updatedFilm);

        assertEquals(1, filmController.allFilms().size());
    }

    @Test
    public void createFilm_withBlankName_ReturnException() {
        film.setName(" ");
        final String expected = "Название фильма не может быть пустым.";

        doTestCreate(film, expected);
    }

    @Test
    public void createFilm_withDescriptionMoreThen200_ReturnException() {
        film.setDescription("a".repeat(201));
        final int maxSize = 200;
        final String expected = "Длина описания не должна быть больше " + maxSize + " символов.";

        doTestCreate(film, expected);
    }

    @Test
    public void createFilm_withIncorrectReleaseDate_ReturnException() {
        final LocalDate lessThenDate = LocalDate.of(1895, 12, 28);
        film.setReleaseDate(lessThenDate.minusDays(1));
        final String expected = "Дата релиза не может быть раньше " + lessThenDate;

        doTestCreate(film, expected);
    }

    @Test
    public void createFilm_WithIncorrectDuration_ReturnException() {
        final String expected = "Продолжительность фильма должна быть положительной";
        // меньше нуля
        film.setDuration(-1);
        doTestCreate(film, expected);
        // ноль
        film.setDuration(0);
        doTestCreate(film, expected);
    }

    @Test
    public void updateFilm_IfFilmListEmpty_ReturnException() {
        assertEquals(0, filmController.allFilms().size(),
                "Не верное количество фильмом");
        final String expected = "Фильма с id=" + film.getId() + " не существует";

        doTestUpdate(film, expected);
    }

    @Test
    public void updateFilm_withBlankName_ReturnException() {
        filmController.createFilm(film);

        film.setName(" ");
        final String expected = "Название фильма не может быть пустым.";

        doTestUpdate(film, expected);
    }

    @Test
    public void updateFilm_withDescriptionMoreThen200_ReturnException() {
        filmController.createFilm(film);

        film.setDescription("a".repeat(201));
        final int maxSize = 200;
        final String expected = "Длина описания не должна быть больше " + maxSize + " символов.";

        doTestUpdate(film, expected);
    }

    @Test
    public void updateFilm_withIncorrectReleaseDate_ReturnException() {
        filmController.createFilm(film);

        final LocalDate lessThenDate = LocalDate.of(1895, 12, 28);
        film.setReleaseDate(lessThenDate.minusDays(1));
        final String expected = "Дата релиза не может быть раньше " + lessThenDate;

        doTestUpdate(film, expected);
    }

    @Test
    public void updateFilm_WithIncorrectDuration_ReturnException() {
        filmController.createFilm(film);

        final String expected = "Продолжительность фильма должна быть положительной";
        // меньше нуля
        film.setDuration(-1);
        doTestUpdate(film, expected);
        // ноль
        film.setDuration(0);
        doTestUpdate(film, expected);
        // null
        film.setDuration(null);
        doTestUpdate(film, expected);
    }

    private void doTestCreate(Film film, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );

        assertEquals(expected, ex.getMessage());
    }

    private void doTestUpdate(Film film, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(film)
        );

        assertEquals(expected, ex.getMessage());
    }
}