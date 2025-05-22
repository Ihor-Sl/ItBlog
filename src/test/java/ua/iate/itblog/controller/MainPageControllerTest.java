package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.mapper.PostMapper;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.service.PostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MainPageControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private Model model;

    @InjectMocks
    private MainPageController mainPageController;

    private List<Post> latestPostsEntity;
    private List<Post> popularPostsEntity;

    private List<PostDto> latestPostsDto;
    private List<PostDto> popularPostsDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Заглушки сущностей Post
        Post post1 = new Post();
        Post post2 = new Post();
        latestPostsEntity = List.of(post1, post2);

        Post post3 = new Post();
        popularPostsEntity = List.of(post3);

        // Заглушки DTO
        PostDto postDto1 = PostDto.builder().build();
        PostDto postDto2 = PostDto.builder().build();
        latestPostsDto = List.of(postDto1, postDto2);

        PostDto postDto3 = PostDto.builder().build();
        popularPostsDto = List.of(postDto3);

        // Моки postService возвращают список сущностей
        when(postService.findTop10ByOrderByCreatedAtDesc()).thenReturn(latestPostsEntity);
        when(postService.findTop10ByOrderByLikedUserIdsDesc()).thenReturn(popularPostsEntity);

        // Моки postMapper конвертируют сущности в DTO
        when(postMapper.mapToDto(latestPostsEntity)).thenReturn(latestPostsDto);
        when(postMapper.mapToDto(popularPostsEntity)).thenReturn(popularPostsDto);
    }

    @Test
    void index_shouldAddLatestAndPopularPostsToModelAndReturnIndexView() {
        String viewName = mainPageController.index(model);

        // Проверяем, что модель получила правильные атрибуты
        verify(model).addAttribute("latestPosts", latestPostsDto);
        verify(model).addAttribute("popularPosts", popularPostsDto);

        // Проверяем имя возвращаемого вида
        assertEquals("index", viewName);
    }
}
