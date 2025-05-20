package ua.iate.itblog.model.comment.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class CommentWithUser {
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String avatar;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
