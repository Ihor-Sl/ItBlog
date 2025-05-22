package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import ua.iate.itblog.model.user.CreateUserRequest;
import ua.iate.itblog.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Captor
    private ArgumentCaptor<CreateUserRequest> createUserRequestCaptor;

    @InjectMocks
    private AuthController authController;

    private BindingResult bindingResult;

    private CreateUserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateUserRequest();
        validRequest.setEmail("newuser@example.com");
        validRequest.setUsername("newuser");
        validRequest.setPassword("password123");

        bindingResult = new BeanPropertyBindingResult(validRequest, "createUserRequest");
    }

    @Test
    void loginGet_shouldReturnLoginView() {
        String view = authController.loginGet();
        assertEquals("auth/login", view);
    }

    @Test
    void registrationGet_shouldAddCreateUserRequestToModelAndReturnRegistrationView() {
        String view = authController.registrationGet(model);
        verify(model).addAttribute(eq("createUserRequest"), any(CreateUserRequest.class));
        assertEquals("auth/registration", view);
    }

    @Test
    void registrationPost_whenEmailExists_shouldRejectEmailAndReturnRegistrationView() {
        when(userService.existsByEmail(validRequest.getEmail())).thenReturn(true);
        when(userService.existsByUsername(validRequest.getUsername())).thenReturn(false);

        String view = authController.registrationPost(validRequest, bindingResult);

        assertTrue(bindingResult.hasFieldErrors("email"));
        assertFalse(bindingResult.hasFieldErrors("username"));
        assertEquals("auth/registration", view);

        verify(userService, never()).createUser(any());
    }

    @Test
    void registrationPost_whenUsernameExists_shouldRejectUsernameAndReturnRegistrationView() {
        when(userService.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userService.existsByUsername(validRequest.getUsername())).thenReturn(true);

        String view = authController.registrationPost(validRequest, bindingResult);

        assertFalse(bindingResult.hasFieldErrors("email"));
        assertTrue(bindingResult.hasFieldErrors("username"));
        assertEquals("auth/registration", view);

        verify(userService, never()).createUser(any());
    }

    @Test
    void registrationPost_whenBothEmailAndUsernameExist_shouldRejectBothAndReturnRegistrationView() {
        when(userService.existsByEmail(validRequest.getEmail())).thenReturn(true);
        when(userService.existsByUsername(validRequest.getUsername())).thenReturn(true);

        String view = authController.registrationPost(validRequest, bindingResult);

        assertTrue(bindingResult.hasFieldErrors("email"));
        assertTrue(bindingResult.hasFieldErrors("username"));
        assertEquals("auth/registration", view);

        verify(userService, never()).createUser(any());
    }

    @Test
    void registrationPost_whenNoErrors_shouldCreateUserAndRedirectToLogin() {
        when(userService.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userService.existsByUsername(validRequest.getUsername())).thenReturn(false);

        String view = authController.registrationPost(validRequest, bindingResult);

        assertFalse(bindingResult.hasErrors());
        assertEquals("redirect:/login", view);

        verify(userService).createUser(createUserRequestCaptor.capture());
        CreateUserRequest capturedRequest = createUserRequestCaptor.getValue();
        assertEquals(validRequest.getEmail(), capturedRequest.getEmail());
        assertEquals(validRequest.getUsername(), capturedRequest.getUsername());
    }
}
