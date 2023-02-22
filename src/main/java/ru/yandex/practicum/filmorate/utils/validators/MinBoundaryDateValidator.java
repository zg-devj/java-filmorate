package ru.yandex.practicum.filmorate.utils.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MinBoundaryDateValidator implements ConstraintValidator<MinBoundaryDate, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(final MinBoundaryDate constraintAnnotation) {
        this.date = LocalDate.parse(constraintAnnotation.date());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if (value.isBefore(date)) {
            return false;
        }
        return true;
    }
}
