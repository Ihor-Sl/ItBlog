package ua.iate.itblog.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.iate.itblog.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static void updateSecurityContext(User user) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                context.getAuthentication().getCredentials(),
                context.getAuthentication().getAuthorities()
        ));
    }
}
