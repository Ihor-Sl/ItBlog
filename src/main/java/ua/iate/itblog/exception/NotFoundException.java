package ua.iate.itblog.exception;

public class NotFoundException extends InternationalizedException {
    public NotFoundException(String message, String... params) {
        super(message, params);
    }
}