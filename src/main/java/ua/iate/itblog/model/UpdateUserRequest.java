package ua.iate.itblog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "{errors.user.username.not-null}")
    @Size(min = 4, max = 64, message = "{errors.user.username.size}")
    private String username;
}