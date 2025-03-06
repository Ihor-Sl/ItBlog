package ua.iate.itblog.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.validation.annotation.ValidFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private int maxSize;
    private Set<String> allowedExtensions;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedExtensions = new HashSet<>(Arrays.asList(constraintAnnotation.allowedExtensions()));
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }
        if (multipartFile.getSize() > maxSize) {
            return false;
        }
        return allowedExtensions.isEmpty() || allowedExtensions.contains(getFileExtension(multipartFile));
    }

    private String getFileExtension(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return null;
        }
        String filename = multipartFile.getOriginalFilename();
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}