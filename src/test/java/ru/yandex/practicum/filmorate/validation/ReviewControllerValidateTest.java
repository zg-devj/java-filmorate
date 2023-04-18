package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ReviewControllerValidateTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void testValidateReview_ContentBlank() {

        Review review = Review.builder()
                .reviewId(1L)
                .content("")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Отзыв не может быть пустым.",
                violations.iterator().next().getMessage());
    }

    @Test
    public void testValidateReview_ContentMoreThen255Chars() {
        Review review = Review.builder()
                .reviewId(1L)
                .content("a".repeat(256))
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Длина отзыва не должна быть больше 255 символов.",
                violations.iterator().next().getMessage());
    }
}
