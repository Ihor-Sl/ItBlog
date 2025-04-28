package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.iate.itblog.model.user.CreateUserRequest;
import ua.iate.itblog.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationGet(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute("createUserRequest") @Valid CreateUserRequest userRequest,
                                   BindingResult bindingResult) {
        if (userService.existsByEmail(userRequest.getEmail())) {
            bindingResult.rejectValue("email", "errors.user.email.exist");
        }
        if (userService.existsByUsername(userRequest.getUsername())) {
            bindingResult.rejectValue("username", "errors.user.username.exist");
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.createUser(userRequest);
        return "redirect:/login";
    }
}