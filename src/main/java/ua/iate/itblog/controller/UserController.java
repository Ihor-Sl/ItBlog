package ua.iate.itblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.iate.itblog.model.UpdateUserRequest;
import ua.iate.itblog.model.User;
import ua.iate.itblog.security.CustomUserDetails;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public String usersGet(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        User user = userService.findById(customUserDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("showEditButton", true);
        return "user";
    }

    @GetMapping("/{id}")
    public String userGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/me/edit")
    public String editUserGet(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        User user = userService.findById(customUserDetails.getUser().getId());
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/me/edit")
    public String editUserPost(@ModelAttribute("user") UpdateUserRequest updateUserRequest,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
                               BindingResult bindingResult) {
        User user = userService.findById(customUserDetails.getUser().getId());
        if (!user.getUsername().equals(updateUserRequest.getUsername()) &&
                userService.existsByUsername(updateUserRequest.getUsername())) {
            bindingResult.rejectValue("username", "errors.user.username.exist");
        }
        if (bindingResult.hasErrors()) {
            return "user-edit";
        }
        User updatedUser = userService.updateUser(updateUserRequest, customUserDetails.getUser().getId());
        SecurityUtils.updateSecurityContext(updatedUser);

        return "redirect:/users/me";
    }
}