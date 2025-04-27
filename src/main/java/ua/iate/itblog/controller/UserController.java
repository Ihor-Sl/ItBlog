package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.iate.itblog.model.UpdateUserRequest;
import ua.iate.itblog.model.User;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.ImageService;
import ua.iate.itblog.service.UserService;

import java.io.InputStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("/me")
    public String meGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        model.addAttribute("user", user);
        model.addAttribute("showEditButton", true);
        model.addAttribute("avatar", imageService.getFileName(user.getAvatar()));
        return "user";
    }

    @GetMapping("/{id}")
    public String userByIdGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("avatar", imageService.getFileName(user.getAvatar()));
        return "user";
    }

    @GetMapping("/me/edit")
    public String meEditGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        model.addAttribute("updateUserRequest", userService.mapToUpdateUserRequest(user));
        model.addAttribute("avatar", imageService.getFileName(user.getAvatar()));
        return "user-edit";
    }

    @PostMapping("/me/edit")
    public String meEditPost(@ModelAttribute("updateUserRequest") @Valid UpdateUserRequest updateUserRequest,
                             BindingResult bindingResult, Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        if (!user.getUsername().equals(updateUserRequest.getUsername()) &&
                userService.existsByUsername(updateUserRequest.getUsername())) {
            bindingResult.rejectValue("username", "errors.user.username.exist");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("updateUserRequest", updateUserRequest);
            return "user-edit";
        }
        User updatedUser = userService.updateUser(updateUserRequest, user.getId());
        SecurityUtils.updateSecurityContext(updatedUser);

        return "redirect:/users/me";
    }

    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable String fileName){
        InputStream avatarStream = imageService.getAvatar(fileName);
        MediaType mediaType = MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "   \"")

                .contentType(mediaType)
                .body(new InputStreamResource(avatarStream));
    }
}