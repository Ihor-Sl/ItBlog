package ua.iate.itblog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.user.CreateUserRequest;
import ua.iate.itblog.model.user.Role;
import ua.iate.itblog.model.user.UpdateUserBannedRequest;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageService imageService;
    @Mock
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindById_existingUser_returnsUser() {
        User user = User.builder().id("1").build();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        User result = userService.findById("1");

        assertEquals(user, result);
    }

    @Test
    void testFindById_nonExistingUser_throwsException() {
        when(userRepository.findById("404")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById("404"));
        assertEquals("errors.user.id.not-found", exception.getMessage());
    }

    @Test
    void testFindByEmail_existingUser_returnsUser() {
        User user = User.builder().email("email@example.com").build();
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("email@example.com");

        assertEquals(user, result);
    }

    @Test
    void testCreateUser_shouldEncodePasswordAndSave() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("email@example.com")
                .password("pass")
                .username("john")
                .build();
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("email@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
        assertEquals("john", saved.getUsername());
        assertTrue(saved.getRoles().contains(Role.ROLE_USER));
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testUpdateUser_withAvatarUpload_shouldSaveUser() {
        User user = User.builder()
                .avatar("oldAvatar.png")
                .username("oldname")
                .location("oldloc")
                .technologyStack(new ArrayList<>())
                .linksToMedia(new ArrayList<>())
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(imageService.upload(file)).thenReturn("newAvatar.png");

        UpdateUserRequest request = UpdateUserRequest.builder()
                .avatar(file)
                .username("newname")
                .location("newloc")
                .technologyStack(new ArrayList<>(List.of("Java", "", "Spring")))
                .linksToMedia(new ArrayList<>(List.of("github.com", "")))
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.update(user, request);

        assertNotNull(updatedUser);
        assertEquals("newname", updatedUser.getUsername());
        assertEquals("newloc", updatedUser.getLocation());
        assertEquals(List.of("Java", "Spring"), updatedUser.getTechnologyStack());
        assertEquals(List.of("github.com"), updatedUser.getLinksToMedia());
        assertEquals("newAvatar.png", updatedUser.getAvatar());

        verify(imageService).delete("oldAvatar.png");
        verify(imageService).upload(file);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateRole_shouldToggleModeratorRole() {
        User user = User.builder().id("1").roles(new HashSet<>(Set.of())).build();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        userService.updateRole("1");
        assertTrue(user.getRoles().contains(Role.ROLE_MODERATOR));

        userService.updateRole("1");
        assertFalse(user.getRoles().contains(Role.ROLE_MODERATOR));
    }

    @Test
    void testUpdateBannedStatus_shouldBanAndClearSessions() {
        User user = User.builder().id("1").email("email@example.com").build();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        UpdateUserBannedRequest req = UpdateUserBannedRequest.builder()
                .bannedUntil(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .build();

        Map mockSessions = Map.of("session1", mock(Session.class));
        when(sessionRepository.findByPrincipalName("email@example.com")).thenReturn(mockSessions);

        userService.updateBannedStatus(req, "1");

        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1), user.getBannedUntil());
        verify(sessionRepository).deleteById("session1");
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateBannedStatus_unban_shouldSetNull() {
        User user = User.builder().id("1").bannedUntil(LocalDateTime.of(2025, 1, 1, 1, 1, 1)).build();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        userService.updateBannedStatus("1");

        assertNull(user.getBannedUntil());
        verify(userRepository).save(user);
    }

    @Test
    void testExistsByEmail_shouldReturnTrue() {
        when(userRepository.existsByEmail("test")).thenReturn(true);
        assertTrue(userService.existsByEmail("test"));
    }

    @Test
    void testExistsByUsername_shouldReturnFalse() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        assertFalse(userService.existsByUsername("john"));
    }

    @Test
    void testHasRoleForEditBanStatus_admin_canBanAnyone() {
        User admin = User.builder().id("admin").roles(Set.of(Role.ROLE_ADMIN)).build();
        User target = User.builder().id("target").roles(Set.of(Role.ROLE_USER)).build();

        when(userRepository.findById("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findById("target")).thenReturn(Optional.of(target));

        assertTrue(userService.hasRoleForEditBanStatus("target", "admin"));
    }

    @Test
    void testHasRoleForEditBanStatus_moderator_canBanUser() {
        User moderator = User.builder().id("mod").roles(Set.of(Role.ROLE_MODERATOR)).build();
        User target = User.builder().id("target").roles(Set.of(Role.ROLE_USER)).build();

        when(userRepository.findById("mod")).thenReturn(Optional.of(moderator));
        when(userRepository.findById("target")).thenReturn(Optional.of(target));

        assertTrue(userService.hasRoleForEditBanStatus("target", "mod"));
    }

    @Test
    void testHasRoleForEditBanStatus_moderatorCannotBanAdmin() {
        User moderator = User.builder().id("mod").roles(Set.of(Role.ROLE_MODERATOR)).build();
        User target = User.builder().id("target").roles(Set.of(Role.ROLE_ADMIN)).build();

        when(userRepository.findById("mod")).thenReturn(Optional.of(moderator));
        when(userRepository.findById("target")).thenReturn(Optional.of(target));

        assertFalse(userService.hasRoleForEditBanStatus("target", "mod"));
    }

    @Test
    void testUserHasBan() {
        UserDto bannedUser = UserDto.builder().bannedUntil(LocalDateTime.of(2025, 1, 1, 1, 1, 1)).build();
        UserDto notBannedUser = UserDto.builder().bannedUntil(null).build();

        assertTrue(userService.userHasBan(bannedUser));
        assertFalse(userService.userHasBan(notBannedUser));
    }
}
