package ua.iate.itblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.iate.itblog.model.User;
import ua.iate.itblog.service.UserService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseBody
    @GetMapping("/index")
    public List<User> index() {
        return userService.findAll();
    }

    @GetMapping("/profile/{id}")
    public String usersGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/{id}/edit")
    public String editUserGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/{id}/edit")
    public String editUserPost(@ModelAttribute("user") User updatedUser, @PathVariable("id") String id,
                               BindingResult bindingResult) {
        if (userService.existsByUsername(updatedUser.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exist!");
        }
        if (bindingResult.hasErrors()) {
            return "edit-profile";
        }

        userService.updateUser(updatedUser, id);
        return "redirect:/profile/" + id;
    }

    @GetMapping("/users/{id}")
    public String userGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users";
    }
}