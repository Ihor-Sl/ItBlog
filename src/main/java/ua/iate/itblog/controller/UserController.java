package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public String meGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        model.addAttribute("user", user);
        model.addAttribute("showEditButton", true);
        return "user";
    }

    @GetMapping("/{id}")
    public String userByIdGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/me/edit")
    public String meEditGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        model.addAttribute("updateUserRequest", userService.mapToUpdateUserRequest(user));
        return "user-edit";
    }

    @PostMapping("/me/edit")
    public String meEditPost(@ModelAttribute("updateUserRequest") @Valid UpdateUserRequest updateUserRequest,
                               BindingResult bindingResult,
                               Model model) {
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
}