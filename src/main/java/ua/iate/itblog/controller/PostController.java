package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.iate.itblog.dto.PostDto;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.mapper.PostMapper;
import ua.iate.itblog.mapper.UserMapper;
import ua.iate.itblog.model.comment.AddCommentRequest;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.PostService;
import ua.iate.itblog.service.UserService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @GetMapping
    public String getAllPosts(@RequestParam(value = "search", defaultValue = "") String search,
                              Pageable pageable,
                              Model model) {
        Page<PostDto> posts = postService.findAll(search, pageable).map(postMapper::mapToDto);
        model.addAttribute("postsPage", posts);
        model.addAttribute("search", search);
        return "post/posts";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable String postId, Model model) {
        PostWithComments post = postService.findWithCommentsById(postId);
        PostDto postDto = postMapper.mapToDto(post);
        Optional<String> currentUserId = SecurityUtils.getCurrentUserId();
        boolean isCurrentUserPostOwner = currentUserId
                .map(userId -> postService.isPostOwner(post.getId(), userId))
                .orElse(false);
        User author = userService.findById(post.getUserId());
        UserDto authorDto = userMapper.mapToDto(author);
        model.addAttribute("author", authorDto);
        model.addAttribute("currentUserId", currentUserId.orElse(null));
        model.addAttribute("post", postDto);
        model.addAttribute("isCurrentUserPostOwner", isCurrentUserPostOwner);
        model.addAttribute("addCommentRequest", new AddCommentRequest());
        return "post/post";
    }

    @GetMapping("/create")
    public String createPostGet(Model model) {
        model.addAttribute("createPostRequest", new CreatePostRequest());
        return "post/create-post";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute("createPostRequest") @Valid CreatePostRequest createPostRequest,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("createPostRequest", createPostRequest);
            return "post/create-post";
        }
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        Post createdPost = postService.createPost(user.getId(), createPostRequest);
        SecurityUtils.updateSecurityContext(user);
        return "redirect:/posts/" + createdPost.getId();
    }

    @PostMapping("/{postId}/delete")
    @PreAuthorize("@postService.isPostOwner(#postId, authentication.principal.user.id)")
    public String deletePost(@PathVariable String postId) {
        postService.delete(postService.findById(postId));
        return "redirect:/users/me";
    }

    @PostMapping("/{postId}/comment")
    public String addComment(@PathVariable String postId,
                             @ModelAttribute @Valid AddCommentRequest addCommentRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return showPost(postId, model);
        }
        String currentUserId = SecurityUtils.getCurrentUserIdOrThrow();
        String commentId = postService.addComment(postId, currentUserId, addCommentRequest).getId();
        return "redirect:/posts/" + postId + "#comment-" + commentId;
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    @PreAuthorize("@postService.isCommentOwner(#commentId, authentication.principal.user.id)")
    public String deleteComment(@PathVariable String commentId, @PathVariable String postId) {
        postService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/like")
    public String like(@PathVariable String postId) {
        postService.like(postId, SecurityUtils.getCurrentUserIdOrThrow());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/dislike")
    public String dislike(@PathVariable String postId) {
        postService.dislike(postId, SecurityUtils.getCurrentUserIdOrThrow());
        return "redirect:/posts/" + postId;
    }
}