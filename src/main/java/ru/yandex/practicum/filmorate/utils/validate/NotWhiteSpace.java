package ru.yandex.practicum.filmorate.utils.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotWhiteSpaceValidator.class)
@Documented
public @interface NotWhiteSpace {
    String message() default "{MinBoundDate.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
