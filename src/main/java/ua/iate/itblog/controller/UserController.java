package ua.iate.itblog.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.iate.itblog.model.User;
import ua.iate.itblog.service.UserService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/index")
    public List<User> index() {
        return userService.findAll();
    }

}
