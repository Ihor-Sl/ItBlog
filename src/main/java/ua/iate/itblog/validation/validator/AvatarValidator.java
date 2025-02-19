package ua.iate.itblog.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.validation.annotation.ValidAvatar;

import java.util.List;

public class AvatarValidator implements ConstraintValidator<ValidAvatar, MultipartFile> {

    private static final long MAX_FILE_SIZE = 5000;

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Avatar can't be more than " + MAX_FILE_SIZE + " MB!")
                    .addConstraintViolation();
            return false;
        }
        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/webp");
        if (!allowedMimeTypes.contains(multipartFile.getContentType())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Avatar must be an image!")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}