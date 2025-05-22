package ua.iate.itblog.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.iate.itblog.dto.CommentDto;
import ua.iate.itblog.model.comment.projection.CommentWithUser;
import ua.iate.itblog.service.ImageService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private CommentMapper commentMapper;

    @Test
    void toDto_WithNullInput_ReturnsEmptyList() {
        List<CommentWithUser> list = null;
        List<CommentDto> result = commentMapper.toDto(list);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDto_WithEmptyList_ReturnsEmptyList() {
        List<CommentDto> result = commentMapper.toDto(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDto_WithCommentList_ReturnsMappedDtoList() {
        LocalDateTime now = LocalDateTime.now();
        CommentWithUser comment1 = CommentWithUser.builder()
                .id("1")
                .postId("post1")
                .userId("user1")
                .username("user1name")
                .avatar("avatar1")
                .content("content1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        CommentWithUser comment2 = CommentWithUser.builder()
                .id("2")
                .postId("post2")
                .userId("user2")
                .username("user2name")
                .avatar("avatar2")
                .content("content2")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(imageService.buildImageUrl("avatar1")).thenReturn("http://image/avatar1");
        when(imageService.buildImageUrl("avatar2")).thenReturn("http://image/avatar2");

        List<CommentDto> result = commentMapper.toDto(List.of(comment1, comment2));

        assertNotNull(result);
        assertEquals(2, result.size());

        CommentDto dto1 = result.get(0);
        assertEquals("1", dto1.getId());
        assertEquals("post1", dto1.getPostId());
        assertEquals("user1", dto1.getUserId());
        assertEquals("user1name", dto1.getUsername());
        assertEquals("http://image/avatar1", dto1.getAvatarUrl());
        assertEquals("content1", dto1.getContent());
        assertEquals(now, dto1.getCreatedAt());
        assertEquals(now, dto1.getUpdatedAt());

        CommentDto dto2 = result.get(1);
        assertEquals("2", dto2.getId());
        assertEquals("post2", dto2.getPostId());
        assertEquals("user2", dto2.getUserId());
        assertEquals("user2name", dto2.getUsername());
        assertEquals("http://image/avatar2", dto2.getAvatarUrl());
        assertEquals("content2", dto2.getContent());
        assertEquals(now, dto2.getCreatedAt());
        assertEquals(now, dto2.getUpdatedAt());
    }

    @Test
    void toDto_SingleComment_ReturnsMappedDto() {
        LocalDateTime now = LocalDateTime.now();
        CommentWithUser comment = CommentWithUser.builder()
                .id("1")
                .postId("post1")
                .userId("user1")
                .username("user1name")
                .avatar("avatar1")
                .content("content1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(imageService.buildImageUrl("avatar1")).thenReturn("http://image/avatar1");

        CommentDto result = commentMapper.toDto(comment);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("post1", result.getPostId());
        assertEquals("user1", result.getUserId());
        assertEquals("user1name", result.getUsername());
        assertEquals("http://image/avatar1", result.getAvatarUrl());
        assertEquals("content1", result.getContent());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void toDto_WithNullAvatar_ReturnsDtoWithNullAvatarUrl() {
        LocalDateTime now = LocalDateTime.now();
        CommentWithUser comment = CommentWithUser.builder()
                .id("1")
                .postId("post1")
                .userId("user1")
                .username("user1name")
                .avatar(null)
                .content("content1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(imageService.buildImageUrl(null)).thenReturn(null);

        CommentDto result = commentMapper.toDto(comment);

        assertNotNull(result);
        assertNull(result.getAvatarUrl());
    }
}