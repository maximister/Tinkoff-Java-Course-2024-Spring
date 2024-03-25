package edu.java.scrapper.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Решил написать отдельную аннотацию, чтобы валидировать ссылки,
 * тк по идее бот и клиент это отдельные приложения, поэтому в теории бот может иметь более новую версию,
 * в которой поддерживаются ссылки, которые в скраппер не добавили по какой-то причине. Поэтому я решил, что
 * такие ссылки стоит отсеять сразу, не засоряя сервис этим кодом валидации.
 * Плюс теперь можно удобно добавлять новые ссылки в ямл файлик
 */
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = ScrapperUrlValidator.class)
public @interface ScrapperUrl {
    String message() default
        "URL must be in supported list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
