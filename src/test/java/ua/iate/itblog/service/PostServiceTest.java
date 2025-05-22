package ua.iate.itblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.mock.web.MockMultipartFile;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.comment.AddCommentRequest;
import ua.iate.itblog.model.comment.Comment;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.repository.CommentRepository;
import ua.iate.itblog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private ImageService imageService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks private PostService postService;

    private Post post;

    @BeforeEach
    void init() {
        post = new Post();
        post.setId("123");
        post.setUserId("user1");
        post.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testFindAll_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAllBy(any(TextCriteria.class), eq(pageable)))
                .thenReturn(Page.empty());

        Page<Post> result = postService.findAll("keyword", pageable);
        assertNotNull(result);
        verify(postRepository).findAllBy(any(TextCriteria.class), eq(pageable));
    }

    @Test
    void testFindAll_WithoutSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<Post> result = postService.findAll(null, pageable);
        assertNotNull(result);
        verify(postRepository).findAll(pageable);
    }

    @Test
    void testFindById_Found() {
        when(postRepository.findById("123")).thenReturn(Optional.of(post));
        Post result = postService.findById("123");
        assertEquals(post, result);
    }

    @Test
    void testFindById_NotFound() {
        when(postRepository.findById("404")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> postService.findById("404"));
    }

    @Test
    void testFindWithCommentsById_Found() {
        PostWithComments withComments = mock(PostWithComments.class);
        when(postRepository.findWithCommentsById("123")).thenReturn(Optional.of(withComments));
        assertEquals(withComments, postService.findWithCommentsById("123"));
    }

    @Test
    void testFindWithCommentsById_NotFound() {
        when(postRepository.findWithCommentsById("404")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> postService.findWithCommentsById("404"));
    }

    @Test
    void testCreatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", "data".getBytes());
        request.setImage(file);

        when(imageService.upload(file)).thenReturn("image.png");
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post created = postService.createPost("user123", request);

        assertEquals("Test Title", created.getTitle());
        assertEquals("user123", created.getUserId());
        assertEquals("image.png", created.getImage());
    }

    @Test
    void testDelete() {
        post.setImage("image.png");

        postService.delete(post);
        verify(imageService).delete("image.png");
        verify(postRepository).delete(post);
        verify(commentRepository).deleteAllByPostId("123");
    }

    @Test
    void testIsPostOwner_True() {
        when(postRepository.existsByIdAndUserId("123", "user1")).thenReturn(true);
        assertTrue(postService.isPostOwner("123", "user1"));
    }

    @Test
    void testIsCommentOwner_True() {
        when(commentRepository.existsByIdAndUserId("c1", "user1")).thenReturn(true);
        assertTrue(postService.isCommentOwner("c1", "user1"));
    }

    @Test
    void testAddComment() {
        AddCommentRequest req = new AddCommentRequest();
        req.setContent("Nice post!");

        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        Comment comment = postService.addComment("123", "user1", req);

        assertEquals("user1", comment.getUserId());
        assertEquals("123", comment.getPostId());
        assertEquals("Nice post!", comment.getContent());
    }

    @Test
    void testDeleteComment() {
        postService.deleteComment("commentId");
        verify(commentRepository).deleteById("commentId");
    }

    @Test
    void testLike_AndUnlike() {
        post.setLikedUserIds(new HashSet<>(List.of("user1")));
        when(postRepository.findById("123")).thenReturn(Optional.of(post));

        postService.like("123", "user1");
        postService.like("123", "user2");

        assertFalse(post.getLikedUserIds().contains("user1"));
        assertTrue(post.getLikedUserIds().contains("user2"));

        verify(postRepository, times(2)).save(post);
    }

    @Test
    void testDislike_AndUndislike() {
        post.setDislikedUserIds(new HashSet<>(Set.of("user1")));
        when(postRepository.findById("123")).thenReturn(Optional.of(post));

        postService.dislike("123", "user1");
        postService.dislike("123", "user2");

        assertFalse(post.getDislikedUserIds().contains("user1"));
        assertTrue(post.getDislikedUserIds().contains("user2"));

        verify(postRepository, times(2)).save(post);
    }

    @Test
    void testFindTop10ByOrderByCreatedAtDesc() {
        List<Post> list = List.of(new Post());
        when(postRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(list);
        assertEquals(list, postService.findTop10ByOrderByCreatedAtDesc());
    }

    @Test
    void testFindTop10ByOrderByLikedUserIdsDesc() {
        List<Post> list = List.of(new Post());
        when(postRepository.findTop10ByOrderByLikedUserIdsDesc()).thenReturn(list);
        assertEquals(list, postService.findTop10ByOrderByLikedUserIdsDesc());
    }
}
