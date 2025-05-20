package ua.iate.itblog.model.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.validation.annotation.ValidFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    private String userId;

    @ValidFile(
            maxSize = 3 * 1024 * 1024,
            allowedExtensions = {"jpeg", "jpg", "webp", "png"},
            message = "{errors.user.avatar.valid-file}"
    )
    private MultipartFile image;

    @Size(min = 3, max = 64, message = "{errors.post.title.size}")
    @NotBlank(message = "{errors.post.title.not-null}")
    private String title;

    @Size(min = 5, max = 1000, message = "{errors.post.content.size}")
    @NotBlank(message = "{errors.post.content.not-null}")
    private String content;
}