package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String usersGet(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("currentUsername", user.getUsername());
        model.addAttribute("technologyStack", user.getTechnologyStack());
        model.addAttribute("socialMediaLinks", user.getLinksToMedia());
        return "user";
    }

    @GetMapping("/{id}")
    public String userGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("technologyStack", user.getTechnologyStack());
        model.addAttribute("socialMediaLinks", user.getLinksToMedia());
        return "user";
    }

    @GetMapping("/me/edit")
    public String editUserGet(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/me/edit")
    public String editUserPost(@ModelAttribute("user") @Valid UpdateUserRequest updateUserRequest,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
                               Model model) {
        if (!customUserDetails.getUser().getUsername().equals(updateUserRequest.getUsername()) && userService.existsByUsername(updateUserRequest.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exist!");
        }
        boolean checkListLinksToMedia = userService.checkListLinksToMedia(updateUserRequest.getLinksToMedia());
        boolean checkTechnologyStack = userService.checkTechnologyStack(updateUserRequest.getTechnologyStack());
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", updateUserRequest);
            return "user-edit";
        }
        User user = userService.updateUser(updateUserRequest, customUserDetails.getUser().getId());
        SecurityUtils.updateSecurityContext(user);

        return "redirect:/users/me";
    }
}