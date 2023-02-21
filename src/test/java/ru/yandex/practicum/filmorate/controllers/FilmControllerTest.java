package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new InMemoryFilmStorage());
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
    public void updateFilm_WithWrongId_ReturnException() {
        assertEquals(0, filmController.allFilms().size(),
                "Не верное количество фильмом");
        final String expected = "Фильма с id=" + film.getId() + " не существует";

        doTestUpdate(film, expected);
    }

    private void doTestUpdate(Film film, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(film)
        );

        assertEquals(expected, ex.getMessage());
    }
}