package ua.iate.itblog.model.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest {

    @Size(max = 64, message = "{errors.user.username.size}")
    private String username;

    @Min(value = 0, message = "{errors.user.age-range}")
    private Integer fromAge;

    @Min(value = 0, message = "{errors.user.age-range}")
    private Integer toAge;

    private List<@Size(max = 128, message = "{errors.user.location.component-size}") String> locations;

    private List<@Size(max = 64, message = "{errors.user.technology-stack.component-size}") String> technologyStack;
}
