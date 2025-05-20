package ua.iate.itblog.model.post.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ua.iate.itblog.model.comment.projection.CommentWithUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class PostWithComments {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentWithUser> comments;
    private Set<String> likedUserIds;
    private Set<String> dislikedUserIds;
}
