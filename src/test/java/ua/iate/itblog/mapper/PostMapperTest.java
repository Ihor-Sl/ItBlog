package ua.iate.itblog.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.iate.itblog.dto.CommentDto;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.model.comment.projection.CommentWithUser;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.service.ImageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @Mock
    private ImageService imageService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private PostMapper postMapper;

    @Test
    void mapToDto_WithNullPostList_ReturnsEmptyList() {
        List<PostDto> result = postMapper.mapToDto((List<Post>) null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToDto_WithEmptyPostList_ReturnsEmptyList() {
        List<PostDto> result = postMapper.mapToDto(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToDto_WithPostList_ReturnsMappedDtoList() {
        LocalDateTime now = LocalDateTime.now();
        Post post1 = Post.builder()
                .id("1")
                .userId("user1")
                .title("title1")
                .content("content1")
                .image("image1")
                .createdAt(now)
                .likedUserIds(Set.of("userA", "userB"))
                .dislikedUserIds(Set.of("userC"))
                .build();

        Post post2 = Post.builder()
                .id("2")
                .userId("user2")
                .title("title2")
                .content("content2")
                .image("image2")
                .createdAt(now)
                .likedUserIds(Set.of("userD"))
                .dislikedUserIds(Set.of("userE", "userF"))
                .build();

        when(imageService.buildImageUrl("image1")).thenReturn("http://image/image1");
        when(imageService.buildImageUrl("image2")).thenReturn("http://image/image2");

        List<PostDto> result = postMapper.mapToDto(List.of(post1, post2));

        assertNotNull(result);
        assertEquals(2, result.size());

        PostDto dto1 = result.get(0);
        assertEquals("1", dto1.getId());
        assertEquals("user1", dto1.getUserId());
        assertEquals("title1", dto1.getTitle());
        assertEquals("content1", dto1.getContent());
        assertEquals("http://image/image1", dto1.getImageUrl());
        assertEquals(now, dto1.getCreatedAt());
        assertEquals(Set.of("userA", "userB"), dto1.getLikedUserIds());
        assertEquals(Set.of("userC"), dto1.getDislikedUserIds());
        assertNull(dto1.getComments());

        PostDto dto2 = result.get(1);
        assertEquals("2", dto2.getId());
        assertEquals("user2", dto2.getUserId());
        assertEquals("title2", dto2.getTitle());
        assertEquals("content2", dto2.getContent());
        assertEquals("http://image/image2", dto2.getImageUrl());
        assertEquals(now, dto2.getCreatedAt());
        assertEquals(Set.of("userD"), dto2.getLikedUserIds());
        assertEquals(Set.of("userE", "userF"), dto2.getDislikedUserIds());
        assertNull(dto2.getComments());
    }

    @Test
    void mapToDto_WithPost_ReturnsMappedDto() {
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder()
                .id("1")
                .userId("user1")
                .title("title1")
                .content("content1")
                .image("image1")
                .createdAt(now)
                .likedUserIds(Set.of("userA", "userB"))
                .dislikedUserIds(Set.of("userC"))
                .build();

        when(imageService.buildImageUrl("image1")).thenReturn("http://image/image1");

        PostDto result = postMapper.mapToDto(post);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals("title1", result.getTitle());
        assertEquals("content1", result.getContent());
        assertEquals("http://image/image1", result.getImageUrl());
        assertEquals(now, result.getCreatedAt());
        assertEquals(Set.of("userA", "userB"), result.getLikedUserIds());
        assertEquals(Set.of("userC"), result.getDislikedUserIds());
        assertNull(result.getComments());
    }

    @Test
    void mapToDto_WithPostWithComments_ReturnsMappedDtoWithComments() {
        LocalDateTime now = LocalDateTime.now();
        PostWithComments post = PostWithComments.builder()
                .id("1")
                .userId("user1")
                .title("title1")
                .content("content1")
                .image("image1")
                .createdAt(now)
                .likedUserIds(Set.of("userA", "userB"))
                .dislikedUserIds(Set.of("userC"))
                .comments(List.of())
                .build();

        List<CommentDto> mockComments = List.of(
                new CommentDto("1", "1", "userX", "usernameX", "avatarUrlX", "comment1", now, now),
                new CommentDto("2", "1", "userY", "usernameY", "avatarUrlY", "comment2", now, now)
        );

        when(imageService.buildImageUrl("image1")).thenReturn("http://image/image1");
        when(commentMapper.toDto((List<CommentWithUser>) any())).thenReturn(mockComments);

        PostDto result = postMapper.mapToDto(post);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals("title1", result.getTitle());
        assertEquals("content1", result.getContent());
        assertEquals("http://image/image1", result.getImageUrl());
        assertEquals(now, result.getCreatedAt());
        assertEquals(Set.of("userA", "userB"), result.getLikedUserIds());
        assertEquals(Set.of("userC"), result.getDislikedUserIds());

        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());
        verify(commentMapper).toDto(post.getComments());
    }

    @Test
    void mapToDto_WithNullImage_ReturnsDtoWithNullImageUrl() {
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder()
                .id("1")
                .userId("user1")
                .title("title1")
                .content("content1")
                .image(null)
                .createdAt(now)
                .likedUserIds(Set.of())
                .dislikedUserIds(Set.of())
                .build();

        when(imageService.buildImageUrl(null)).thenReturn(null);

        PostDto result = postMapper.mapToDto(post);

        assertNotNull(result);
        assertNull(result.getImageUrl());
    }

    @Test
    void mapToDto_WithNullLikedAndDislikedIds_ReturnsDtoWithEmptySets() {
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder()
                .id("1")
                .userId("user1")
                .title("title1")
                .content("content1")
                .image("image1")
                .createdAt(now)
                .likedUserIds(Set.of())
                .dislikedUserIds(Set.of())
                .build();

        when(imageService.buildImageUrl("image1")).thenReturn("http://image/image1");

        PostDto result = postMapper.mapToDto(post);

        assertNotNull(result);
        assertNotNull(result.getLikedUserIds());
        assertTrue(result.getLikedUserIds().isEmpty());
        assertNotNull(result.getDislikedUserIds());
        assertTrue(result.getDislikedUserIds().isEmpty());
    }
}