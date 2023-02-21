package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

class UserControllerValidateTest {

    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void test_WrongEmail() {
        LocalDate birthday = LocalDate.now().minusDays(1);
        User user = new User(null, "example.com", "login",
                "name", birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Не является адресом электронной почты."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_WrongWhiteSpaceEmail() {
        LocalDate birthday = LocalDate.now().minusDays(1);
        User user = new User(null, " ", "login",
                "name", birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Не является адресом электронной почты."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_EmptyEmail() {
        LocalDate birthday = LocalDate.now().minusDays(1);
        User user = new User(null, "", "login",
                "name", birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Адрес электронной почты не может быть пустым."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_BlankLogin() {
        LocalDate birthday = LocalDate.now().minusDays(1);
        User user = new User(null, "name@example.com",
                "name", "", birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Логин не может быть пустым."
                , violations.iterator().next().getMessage());
    }

    @Test
    public void test_WhiteSpaceLogin() {
        LocalDate birthday = LocalDate.now().minusDays(1);
        User user = new User(null, "name@example.com",
                "name", " ", birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Логин не должен содержать пробелы."
                , violations.iterator().next().getMessage());
    }
}
