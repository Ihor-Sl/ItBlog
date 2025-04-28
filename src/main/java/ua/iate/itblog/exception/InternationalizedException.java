package ua.iate.itblog.exception;

import lombok.Getter;

@Getter
public abstract class InternationalizedException extends RuntimeException {

    private final String[] params;

    public InternationalizedException(String message, String... params) {
        super(message);
        this.params = params;
    }
}
