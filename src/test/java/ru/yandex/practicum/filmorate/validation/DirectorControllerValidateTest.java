package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class DirectorControllerValidateTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void testValidateDirector_NameBlank() {
        Director director = Director.builder()
                .id(1)
                .build();

        Set<ConstraintViolation<Director>> violations = validator.validate(director);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Имя директора не может быть пустым.",
                violations.iterator().next().getMessage());
    }

    @Test
    public void testValidateDirector_NameMoreThen255Chars() {
        Director director = Director.builder()
                .id(1)
                .name("a".repeat(51))
                .build();

        Set<ConstraintViolation<Director>> violations = validator.validate(director);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Длина имени режисера не должна быть больше 50 символов.",
                violations.iterator().next().getMessage());
    }
}
