package ua.iate.itblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.mapper.PostMapper;
import ua.iate.itblog.service.PostService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class MainPageController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping
    public String index(Model model) {
        List<PostDto> latestPosts = postMapper.mapToDto(postService.findTop10ByOrderByCreatedAtDesc());
        List<PostDto> popularPosts = postMapper.mapToDto(postService.findTop10ByOrderByLikedUserIdsDesc());
        model.addAttribute("latestPosts", latestPosts);
        model.addAttribute("popularPosts", popularPosts);
        return "index";
    }
}
