package ua.iate.itblog.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.User;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static String getCurrentUserIdOrThrow() {
        return getCurrentUserId().orElseThrow(() -> new NotFoundException("errors.user.not-found"));
    }

    public static Optional<String> getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getUser)
                .map(User::getId);
    }

    public static void updateSecurityContext(User user) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                context.getAuthentication().getCredentials(),
                context.getAuthentication().getAuthorities()
        ));
    }
}