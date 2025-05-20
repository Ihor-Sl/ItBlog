package ua.iate.itblog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {
        String email = request.getParameter("email");

        if (exception instanceof LockedException && email != null) {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                LocalDateTime bannedUntil = optionalUser.get().getBannedUntil();
                request.getSession().setAttribute("bannedUntil", bannedUntil);
                response.sendRedirect("/login?error=locked");
                return;
            }
        }

        if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/login?error=bad_credentials");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}