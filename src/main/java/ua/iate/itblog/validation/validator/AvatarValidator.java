package ua.iate.itblog.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.validation.annotation.ValidFile;

import java.util.Arrays;
import java.util.List;

public class AvatarValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private int maxSize;
    private List<String> allowedExtensions;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedExtensions = Arrays.asList(constraintAnnotation.allowedExtensions());
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }
        if (multipartFile.getSize() > maxSize) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Avatar can't be more than " + maxSize + " MB!")
                    .addConstraintViolation();
            return false;
        }
        if (!allowedExtensions.contains(multipartFile.getContentType())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Avatar must be an image!")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}