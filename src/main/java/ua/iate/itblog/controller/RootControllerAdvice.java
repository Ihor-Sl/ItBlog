package ua.iate.itblog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.iate.itblog.exception.NotFoundException;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RootControllerAdvice {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public String handleNotFoundException(NotFoundException e, Model model, Locale locale) {
        model.addAttribute("message", messageSource.getMessage(e.getMessage(), e.getParams(), locale));

        return "not-found";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public String handleException(Exception e) {
        log.error(e.getMessage(), e);
        return "server-error";
    }
}