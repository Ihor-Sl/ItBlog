package ua.iate.itblog.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.iate.itblog.validation.validator.TechnologyStackValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TechnologyStackValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTechnologyStack {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}