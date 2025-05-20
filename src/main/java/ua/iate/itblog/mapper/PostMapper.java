package ua.iate.itblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.service.ImageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final ImageService imageService;
    private final CommentMapper commentMapper;

    public List<PostDto> mapToDto(List<PostWithComments> post) {
        if (post == null) {
            return List.of();
        }
        return post.stream()
                .map(this::mapToDto)
                .toList();
    }

    public PostDto mapToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(imageService.buildImageUrl(post.getImage()))
                .createdAt(post.getCreatedAt())
                .likedUserIds(post.getLikedUserIds())
                .dislikedUserIds(post.getDislikedUserIds())
                .build();
    }

    public PostDto mapToDto(PostWithComments post) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(imageService.buildImageUrl(post.getImage()))
                .createdAt(post.getCreatedAt())
                .comments(commentMapper.toDto(post.getComments()))
                .likedUserIds(post.getLikedUserIds())
                .dislikedUserIds(post.getDislikedUserIds())
                .build();
    }
}
