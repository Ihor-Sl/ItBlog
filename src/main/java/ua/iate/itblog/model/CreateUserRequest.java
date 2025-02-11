package ua.iate.itblog.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Email cannot be empty!")
    @Email(message = "Invalid email format!")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long!")
    private String password;

    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 4, max = 64, message = "Username must be between 4 and 64 characters!")
    private String username;
}