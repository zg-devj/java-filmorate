package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

public class FilmControllerValidateTest {

    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void test_BlankName() {
        LocalDate now = LocalDate.now().minusYears(1);
        Film film = new Film(null, " ", "description",
                now, 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Название фильма не может быть пустым."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_DescriptionMoreThen200Chars() {
        LocalDate now = LocalDate.now().minusYears(1);
        String description = "a".repeat(201);
        Film film = new Film(null, "name", description,
                now, 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Длина описания не должна быть больше 200 символов."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_PositiveDuration() {
        LocalDate now = LocalDate.now().minusYears(1);
        Film film = new Film(null, "name", "description",
                now, -1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Продолжительность фильма должна быть положительной."
                , violations.iterator().next().getMessage());
    }
}
