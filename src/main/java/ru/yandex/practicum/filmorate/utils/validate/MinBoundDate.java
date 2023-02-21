package ru.yandex.practicum.filmorate.utils.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.time.LocalDate;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateMinBoundaryValidator.class)
@Documented
public @interface MinBoundDate {
    String message() default "{MinBoundDate.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String date();

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        MinBoundDate[] value();
    }
}
