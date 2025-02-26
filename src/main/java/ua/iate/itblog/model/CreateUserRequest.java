package ua.iate.itblog.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "{errors.user.email.not-null}")
    @Email(message = "{errors.user.email.size}")
    private String email;

    @Size(min = 8, message = "{errors.user.password.size}")
    @NotNull(message = "{errors.user.password.not-null}")
    private String password;

    @NotBlank(message = "{errors.user.username.not-null}")
    @Size(min = 4, max = 64, message = "{errors.user.username.size}")
    private String username;
}