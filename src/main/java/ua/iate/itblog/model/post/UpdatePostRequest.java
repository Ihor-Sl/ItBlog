package ua.iate.itblog.model.post;

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
public class UpdatePostRequest {
  @Size(min = 3, max = 64, message = "{errors.post.title.size}")
  @NotBlank(message = "{errors.post.title.not-null}")
  private String title;

  @Size(min = 5, max = 1000, message = "{errors.post.content.size}")
  @NotBlank(message = "{errors.post.content.not-null}")
  private String content;
}