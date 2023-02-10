package ru.yandex.practicum.filmorate.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidateServiceTest {

    static Film wrongFilm;
    static User wrongUser;
    static User normalUser;

    private FilmController filmController = new FilmController();

    @BeforeAll
    static void beforeAll() {
        String description = "В Java есть инструменты для проверки корректности различных данных. " +
                "С помощью аннотаций можно задать ограничения, которые будут проверяться " +
                "автоматически. Для этого добавьте в описание сборки проекта следующую зависимость.";
        wrongFilm = new Film(1, " ", description,
                LocalDate.of(1895, 12, 27), -1);

        wrongUser = new User(1, "user@exa@mple.com", "login",
                " ", LocalDate.of(2500, 01, 01));

        normalUser = new User(2, "user@example.com", "login",
                "User Name", LocalDate.of(2000, 01, 01));

    }

    @Test
    public void test_isEmptyStringField() {

        final String expected = "Название фильма не может быть пустым.";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService.isEmptyStringField(wrongFilm.getName(), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_checkMaxSizeStringField() {
        final int maxSize = 200;
        final String expected = "Длина описания не должна быть больше " + maxSize + " символов.";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .checkMaxSizeStringField(wrongFilm.getDescription(), maxSize, expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_dateLessThen() {
        final LocalDate lessThenDate = LocalDate.of(1895, 12, 28);
        final String expected =
                String.format("Дата релиза не может быть раньше %s", lessThenDate);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .dateLessThen(wrongFilm.getReleaseDate(), lessThenDate, expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_durationMoreThenZero() {
        final String expected = "Продолжительность фильма должна быть положительной";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .durationMoreThenZero(wrongFilm.getDuration(), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_isEmptyList() {
        final String expected = "Фильмов не существует";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .isEmptyList(filmController.allFilms().size(), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_containsFilm() {
        Film filmNoAdded = new Film(999, "Film 1", "Desc",
                LocalDate.of(2022, 01, 01), 100);
        final String expected = "фильма с id=" + filmNoAdded.getId() + " не существует";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .containsFilm(!filmController.allFilms().contains(filmNoAdded), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_isNotEmail() {
        final String expected = "Не является адресом электронной почты.";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .isNotEmail(wrongUser.getEmail(), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_dateLaterThenNow() {
        final String expected = "День рождения не может быть в будущем.";

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> ValidateService
                        .dateLaterThenNow(wrongUser.getBirthday(), expected)
        );

        assertEquals(expected, ex.getMessage());
    }

    @Test
    public void test_ifStringIsNullOrEmpty_WithWrongUserName() {
        final String expected = wrongUser.getLogin();
        final String actual = ValidateService.
                ifStringIsNullOrEmpty(wrongUser.getName(), wrongUser.getLogin());
        assertEquals(expected, actual);
    }

    @Test
    public void test_ifStringIsNullOrEmpty_WithNormalUserName() {
        final String expected = normalUser.getName();
        final String actual = ValidateService.
                ifStringIsNullOrEmpty(normalUser.getName(), normalUser.getLogin());
        assertEquals(expected, actual);
    }
}