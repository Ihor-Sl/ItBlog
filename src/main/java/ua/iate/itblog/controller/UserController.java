package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.mapper.UserMapper;
import ua.iate.itblog.model.user.*;
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
        model.addAttribute("updateUserBannedRequest", null);
        model.addAttribute("currentUser", user);
        model.addAttribute("userHasBan", false);
        return "user/user";
    }

    @GetMapping("/{id}")
    public String userByIdGet(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        UserDto userDto = userMapper.mapToDto(user);
        User currentUser = userService.findById(SecurityUtils.getCurrentUserIdOrThrow());
        model.addAttribute("user", userDto);
        model.addAttribute("avatar", imageService.buildImageUrl(user.getAvatar()));
        model.addAttribute("updateUserBannedRequest", new UpdateUserBannedRequest());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userHasBan", userService.userHasBan(userDto));
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

    @PostMapping("/{id}/edit-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String editRolePost(@PathVariable("id") String id,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }
        userService.updateRole(id);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("@userService.hasRoleForEditBanStatus(#id, authentication.principal.user.id)")
    public String block(@PathVariable("id") String id,
    @ModelAttribute("updateUserBannedRequest") UpdateUserBannedRequest updateUserBannedRequest,
                                  BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("updateUserBannedRequest", updateUserBannedRequest);
            return "redirect:/users/" + id;
        }
        userService.updateBannedStatus(updateUserBannedRequest, id);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("@userService.hasRoleForEditBanStatus(#id, authentication.principal.user.id)")
    public String unblock(@PathVariable("id") String id, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "redirect:/users/" + id;
        }
        userService.updateBannedStatus(null, id);
        return "redirect:/users/" + id;
    }
}