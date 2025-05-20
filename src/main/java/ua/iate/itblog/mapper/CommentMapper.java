package ua.iate.itblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.iate.itblog.dto.CommentDto;
import ua.iate.itblog.model.comment.projection.CommentWithUser;
import ua.iate.itblog.service.ImageService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ImageService imageService;

    public List<CommentDto> toDto(List<CommentWithUser> comments) {
        if (comments == null) {
            return List.of();
        }
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto toDto(CommentWithUser comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .username(comment.getUsername())
                .avatarUrl(imageService.buildImageUrl(comment.getAvatar()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
