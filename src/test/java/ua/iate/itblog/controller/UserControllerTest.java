package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.mapper.UserMapper;
import ua.iate.itblog.model.user.UpdateUserBannedRequest;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.model.user.UserSearchRequest;
import ua.iate.itblog.security.SecurityUtils;
import ua.iate.itblog.service.ImageService;
import ua.iate.itblog.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_validRequest_shouldReturnUserPage() {
        UserSearchRequest searchRequest = new UserSearchRequest();
        Pageable pageable = Pageable.unpaged();

        User user = new User();
        user.setId("1");
        List<User> users = List.of(user);

        Page<User> page = new PageImpl<>(users);

        when(userService.findAll(searchRequest, pageable)).thenReturn(page);
        when(userMapper.mapToDto(user)).thenReturn(new UserDto());

        String view = userController.getAllUsers(searchRequest, bindingResult, pageable, model);

        assertEquals("user/users", view);
        verify(model).addAttribute(eq("userSearchRequest"), eq(searchRequest));
        verify(model).addAttribute(eq("usersPage"), any(Page.class));
    }

    @Test
    void getAllUsers_bindingErrors_shouldReturnEmptyPage() {
        UserSearchRequest searchRequest = new UserSearchRequest();
        Pageable pageable = Pageable.unpaged();

        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.getAllUsers(searchRequest, bindingResult, pageable, model);

        assertEquals("user/users", view);
        verify(model).addAttribute("userSearchRequest", searchRequest);
        verify(model).addAttribute(eq("usersPage"), any(PageImpl.class));
    }

    @Test
    void meGet_shouldReturnCurrentUserView() {
        String currentUserId = "user123";

        User user = new User();
        user.setId(currentUserId);
        user.setUsername("user");

        UserDto userDto = new UserDto();
        userDto.setId(currentUserId);
        userDto.setUsername("user");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(currentUserId);
            when(userService.findById(currentUserId)).thenReturn(user);
            when(userMapper.mapToDto(user)).thenReturn(userDto);

            String view = userController.meGet(model);

            assertEquals("user/user", view);
            verify(model).addAttribute("user", userDto);
            verify(model).addAttribute("showEditButton", true);
            verify(model).addAttribute("updateUserBannedRequest", null);
            verify(model).addAttribute("currentUser", user);
            verify(model).addAttribute("userHasBan", false);
        }
    }

    @Test
    void meEditGet_shouldReturnUserEditView() {
        String currentUserId = "userId";
        User user = new User();
        user.setId(currentUserId);
        user.setAvatar("avatarId");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(currentUserId);
            when(userService.findById(currentUserId)).thenReturn(user);
            when(userMapper.mapToUpdateRequest(user)).thenReturn(updateUserRequest);
            when(imageService.buildImageUrl("avatarId")).thenReturn("avatarUrl");

            String view = userController.meEditGet(model);

            assertEquals("user/user-edit", view);
            verify(model).addAttribute("updateUserRequest", updateUserRequest);
            verify(model).addAttribute("avatarUrl", "avatarUrl");
        }
    }

    @Test
    void meEditPost_validUpdate_shouldRedirectToMe() {
        String currentUserId = "userId";
        User user = new User();
        user.setId(currentUserId);
        user.setUsername("oldName");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("newName");

        User updatedUser = new User();
        updatedUser.setId(currentUserId);
        updatedUser.setUsername("newName");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(currentUserId);
            when(userService.findById(currentUserId)).thenReturn(user);
            when(userService.existsByUsername("newName")).thenReturn(false);
            when(userService.update(user, updateRequest)).thenReturn(updatedUser);

            when(bindingResult.hasErrors()).thenReturn(false);

            String view = userController.meEditPost(updateRequest, bindingResult, model);

            assertEquals("redirect:/users/me", view);
            verify(SecurityUtils.class);
        }
    }

    @Test
    void editRolePost_shouldRedirect() {
        String userId = "userId";
        String redirect = userController.editRolePost(userId);
        verify(userService).updateRole(userId);
        assertEquals("redirect:/users/" + userId, redirect);
    }

    @Test
    void block_withErrors_shouldRedirectBack() {
        String userId = "userId";
        UpdateUserBannedRequest bannedRequest = new UpdateUserBannedRequest();

        when(bindingResult.hasErrors()).thenReturn(true);

        String redirect = userController.block(userId, bannedRequest, bindingResult, model);

        assertEquals("redirect:/users/" + userId, redirect);
        verify(model).addAttribute("updateUserBannedRequest", bannedRequest);
        verify(userService, never()).updateBannedStatus(any(), anyString());
    }

    @Test
    void unblock_shouldCallServiceAndRedirect() {
        String userId = "userId";

        String redirect = userController.unblock(userId);

        assertEquals("redirect:/users/" + userId, redirect);
        verify(userService).updateBannedStatus(userId);
    }

    @Test
    void meEditPost_usernameExists_shouldAddErrorAndReturnForm() {
        String currentUserId = "userId";
        User user = new User();
        user.setId(currentUserId);
        user.setUsername("oldName");

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("existingName");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(currentUserId);
            when(userService.findById(currentUserId)).thenReturn(user);
            when(userService.existsByUsername("existingName")).thenReturn(true);
            doAnswer(invocation -> {
                when(bindingResult.hasErrors()).thenReturn(true);
                return null;
            }).when(bindingResult).rejectValue(anyString(), anyString());
            when(bindingResult.hasErrors()).thenReturn(false); // на момент проверки до rejectValue
            String view = userController.meEditPost(updateRequest, bindingResult, model);
            assertEquals("user/user-edit", view);
            verify(bindingResult).rejectValue("username", "errors.user.username.exist");
            verify(model).addAttribute("updateUserRequest", updateRequest);
        }
    }
}