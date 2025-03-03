package ua.iate.itblog.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String[] params;

    public NotFoundException(String message, String... params) {
        super(message);
        this.params = params;
    }
}