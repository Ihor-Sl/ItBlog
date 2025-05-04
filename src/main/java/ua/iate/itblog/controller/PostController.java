package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.UpdatePostRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.PostService;
import ua.iate.itblog.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/{postId}")
    public String showPost(@PathVariable String postId, Model model) {
        Post post = postService.findById(postId);
        boolean isCurrentUserPostOwner = SecurityUtils.getCurrentUserId()
                .map(userId -> postService.isPostOwner(post.getId(), userId))
                .orElse(false);
        model.addAttribute("user", userService.findById(post.getUserId()));
        model.addAttribute("post", post);
        model.addAttribute("isCurrentUser", isCurrentUserPostOwner);
        return "post";
    }

    @GetMapping("/create-post")
    public String createPostGet(Model model) {
        model.addAttribute("createPostRequest", new CreatePostRequest());
        return "create-post";
    }

    @PostMapping("/create-post")
    public String createPost(@ModelAttribute @Valid CreatePostRequest createPostRequest,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("createPostRequest", createPostRequest);
            return "create-post";
        }
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        Post createdPost = postService.createPost(user.getId(), createPostRequest);
        SecurityUtils.updateSecurityContext(user);
        return "redirect:/posts/" + createdPost.getId();
    }

    @GetMapping("/{postId}/edit-post")
    public String editPostGet(@PathVariable String postId, Model model) {
        model.addAttribute("updatePostRequest", new UpdatePostRequest());
        model.addAttribute("postId", postId);
        return "edit-post";
    }

    @PostMapping("/{postId}/edit-post")
    @PreAuthorize("@postService.isPostOwner(#postId, authentication.principal.user.id)")
    public String editPost(@ModelAttribute @Valid UpdatePostRequest updatePostRequest,
                           BindingResult bindingResult,
                           @PathVariable String postId, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("updatePostRequest", updatePostRequest);
            model.addAttribute("postId", postId);
            return "edit-post";
        }
        Post updatedPost = postService.updatePost(postId, updatePostRequest);
        return "redirect:/posts/" + updatedPost.getId();
    }

    @PostMapping("/{postId}/delete")
    @PreAuthorize("@postService.isPostOwner(#postId, authentication.principal.user.id)")
    public String deletePost(@PathVariable String postId) {
        postService.delete(postService.findById(postId));
        return "redirect:/users/me";
    }
}