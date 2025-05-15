package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.mapper.UserMapper;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.model.user.UserSearchRequest;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.ImageService;
import ua.iate.itblog.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final ImageService imageService;

    @GetMapping
    public String getAllUsers(@Valid UserSearchRequest userSearchRequest, BindingResult bindingResult,
                              Pageable pageable, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userSearchRequest", userSearchRequest);
            model.addAttribute("usersPage", new PageImpl<>(List.of()));
            return "user/users";
        }

        Page<UserDto> users = userService.findAll(userSearchRequest, pageable)
                .map(userMapper::mapToDto);
        model.addAttribute("userSearchRequest", userSearchRequest);
        model.addAttribute("usersPage", users);
        return "user/users";
    }

    @GetMapping("/me")
    public String meGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        UserDto userDto = userMapper.mapToDto(user);
        model.addAttribute("user", userDto);
        model.addAttribute("showEditButton", true);
        return "user/user";
    }

    @GetMapping("/{id}")
    public String userByIdGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        UserDto userDto = userMapper.mapToDto(user);
        model.addAttribute("user", userDto);
        model.addAttribute("avatar", imageService.buildImageUrl(user.getAvatar()));
        return "user/user";
    }

    @GetMapping("/me/edit")
    public String meEditGet(Model model) {
        User user = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        UpdateUserRequest updateUserRequest = userMapper.mapToUpdateRequest(user);
        model.addAttribute("updateUserRequest", updateUserRequest);
        model.addAttribute("avatarUrl", imageService.buildImageUrl(user.getAvatar()));
        return "user/user-edit";
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
            return "user/user-edit";
        }
        User updatedUser = userService.update(user, updateUserRequest);
        SecurityUtils.updateSecurityContext(updatedUser);

        return "redirect:/users/me";
    }
}