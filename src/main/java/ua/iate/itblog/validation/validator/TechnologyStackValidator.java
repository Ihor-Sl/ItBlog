package ua.iate.itblog.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.iate.itblog.validation.annotation.ValidTechnologyStack;

import java.util.List;
import java.util.regex.Pattern;

public class TechnologyStackValidator implements ConstraintValidator<ValidTechnologyStack, List<String>> {

    private static final Pattern VALID_TECH_PATTERN = Pattern.compile("^\\s*\\S+");
    private static final int MAX_LENGTH = 64;

    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        if (strings == null || strings.isEmpty()) {
            return true;
        }
        for (String tech : strings) {
            if (tech == null || tech.trim().isEmpty()) {
                continue;
            }
            if (tech.length() > MAX_LENGTH || !VALID_TECH_PATTERN.matcher(tech).matches()) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("Technology stack entry cannot be more than " + MAX_LENGTH + " characters!")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}