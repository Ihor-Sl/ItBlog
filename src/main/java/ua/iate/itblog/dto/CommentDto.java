package ua.iate.itblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
