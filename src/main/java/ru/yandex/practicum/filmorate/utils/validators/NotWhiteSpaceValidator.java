package ru.yandex.practicum.filmorate.utils.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotWhiteSpaceValidator implements ConstraintValidator<NotWhiteSpace, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        return true;
    }
}
