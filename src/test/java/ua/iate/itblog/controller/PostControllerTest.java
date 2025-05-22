package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.mapper.PostMapper;
import ua.iate.itblog.mapper.UserMapper;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.service.PostService;
import ua.iate.itblog.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostControllerTest {

    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPosts_shouldAddPostsPageAndSearchToModel() {
        String search = "query";
        Pageable pageable = PageRequest.of(0, 10);

        Post post = new Post();
        Page<Post> postsPage = new PageImpl<>(List.of(post), pageable, 1);
        PostDto postDto = PostDto.builder().build();
        Page<PostDto> postsDtoPage = new PageImpl<>(List.of(postDto), pageable, 1);

        when(postService.findAll(eq(search), eq(pageable))).thenReturn(postsPage);
        when(postMapper.mapToDto(any(Post.class))).thenReturn(postDto);

        String view = postController.getAllPosts(search, pageable, model);

        verify(model).addAttribute("postsPage", postsDtoPage);
        verify(model).addAttribute("search", search);
        assertEquals("post/posts", view);
    }

    @Test
    void createPostGet_shouldAddCreatePostRequestAndReturnView() {
        String view = postController.createPostGet(model);
        verify(model).addAttribute(eq("createPostRequest"), any(CreatePostRequest.class));
        assertEquals("post/create-post", view);
    }

    @Test
    void createPost_withBindingErrors_shouldReturnCreatePostView() {
        CreatePostRequest request = new CreatePostRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = postController.createPost(request, bindingResult, model);

        verify(model).addAttribute("createPostRequest", request);
        assertEquals("post/create-post", view);
    }

    @Test
    void deletePost_shouldDeleteAndRedirect() {
        String postId = "postId";
        Post post = new Post();
        when(postService.findById(postId)).thenReturn(post);

        String redirect = postController.deletePost(postId);

        verify(postService).delete(post);
        assertEquals("redirect:/users/me", redirect);
    }

    @Test
    void deleteComment_shouldDeleteAndRedirect() {
        String postId = "postId";
        String commentId = "commentId";

        String redirect = postController.deleteComment(commentId, postId);

        verify(postService).deleteComment(commentId);
        assertEquals("redirect:/posts/" + postId, redirect);
    }
}