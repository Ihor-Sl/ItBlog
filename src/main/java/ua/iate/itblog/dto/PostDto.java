package ua.iate.itblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<CommentDto> comments;
    private Set<String> likedUserIds;
    private Set<String> dislikedUserIds;
}
