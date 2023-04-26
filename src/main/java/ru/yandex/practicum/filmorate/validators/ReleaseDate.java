package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ReleaseDate {
    static final String earlyDate = "1895-12-28";

    String message() default "Дата релиза не может быть раньше {value}";

    String value() default earlyDate;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
