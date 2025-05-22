package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import ua.iate.itblog.exception.NotFoundException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RootControllerAdviceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @InjectMocks
    private RootControllerAdvice rootControllerAdvice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleNotFoundException_shouldAddMessageToModelAndReturnErrorView() {
        String messageKey = "error.notfound";
        String[] params = {"param1"};
        Locale locale = Locale.ENGLISH;
        NotFoundException exception = new NotFoundException(messageKey, params);
        String localizedMessage = "Resource not found";
        when(messageSource.getMessage(messageKey, params, locale)).thenReturn(localizedMessage);
        String viewName = rootControllerAdvice.handleNotFoundException(exception, model, locale);
        verify(messageSource).getMessage(messageKey, params, locale);
        verify(model).addAttribute("message", localizedMessage);
        assertEquals("error/404", viewName);
    }
}