package ua.iate.itblog.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.iate.itblog.validation.validator.AvatarValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AvatarValidator.class})
public @interface ValidAvatar {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}