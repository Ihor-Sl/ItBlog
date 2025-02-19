package ua.iate.itblog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.validation.annotation.ValidTechnologyStack;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 4, max = 64, message = "Username must be between 4 and 64 characters!")
    private String username;

    private MultipartFile avatar;

    @Past(message = "Date of birth must be in the past!")
    private LocalDate dateOfBirth;

    @Size(max = 128, message = "Location can't be more than 128 characters!")
    private String location;

    @Size(max = 10, message = "The size of technology stack can't be more than 10!")
    @ValidTechnologyStack
    private List<String> technologyStack;

    @Size(max = 5, message = "The size of links of median can't be more than 5!")
    private List<@URL String> linksToMedia;
}