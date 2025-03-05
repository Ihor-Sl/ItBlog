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
import ua.iate.itblog.validation.annotation.ValidFile;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "{errors.user.username.not-null}")
    @Size(min = 4, max = 64, message = "{errors.user.username.size}")
    private String username;

    @ValidFile(maxSize = 3 * 1024 * 1024, allowedExtensions = {"jpeg", "webp", "png"}, message = "{errors.user.avatar.valid-file}")
    private MultipartFile avatar;

    @Past(message = "{errors.user.date-of-birth}")
    private LocalDate dateOfBirth;

    @Size(max = 128, message = "{errors.user.location}")
    private String location;

    @Size(max = 10, message = "{errors.user.technology-stack.stack-size}")
    private List<@Size(max = 64, message = "{errors.user.technology-stack.component-size}") String> technologyStack;

    @Size(max = 5, message = "{errors.user.social-media-links.size}")
    private List<@URL(message = "{errors.user.social-media.url}") String> linksToMedia;
}