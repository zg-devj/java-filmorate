package ru.yandex.practicum.filmorate.utils.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinBoundaryDateValidator.class)
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
