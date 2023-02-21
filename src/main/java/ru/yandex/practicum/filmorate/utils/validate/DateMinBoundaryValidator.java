package ru.yandex.practicum.filmorate.utils.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateMinBoundaryValidator implements ConstraintValidator<MinBoundDate, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(final MinBoundDate constraintAnnotation) {
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
