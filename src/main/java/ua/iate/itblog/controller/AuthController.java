package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.iate.itblog.model.CreateUserRequest;
import ua.iate.itblog.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "login";
    }

    @GetMapping("/registration")
    public String registrationGet(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute("user") @Valid CreateUserRequest userRequest,
                                   BindingResult bindingResult) {
        if (userService.existsByEmail(userRequest.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email already exist!");
        }
        if (userService.existsByUsername(userRequest.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exist!");
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.createUser(userRequest);
        return "redirect:/login";
    }
}