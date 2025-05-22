package ua.iate.itblog.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.model.user.Role;
import ua.iate.itblog.service.ImageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void mapToDto_WithNullList_ReturnsEmptyList() {
        List<User> users = null;
        List<UserDto> result = userMapper.mapToDto(users);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToDto_WithEmptyList_ReturnsEmptyList() {
        List<UserDto> result = userMapper.mapToDto(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToDto_WithUserList_ReturnsMappedDtoList() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dob = LocalDate.of(1990, 1, 1);

        User user1 = User.builder()
                .id("1")
                .username("user1")
                .avatar("avatar1.png")
                .dateOfBirth(dob)
                .location("Location1")
                .bannedUntil(now.plusDays(1))
                .roles(Set.of(Role.ROLE_USER))
                .technologyStack(List.of("Java", "Spring"))
                .linksToMedia(List.of("github.com/user1"))
                .createdAt(now.toLocalDate())
                .build();

        User user2 = User.builder()
                .id("2")
                .username("user2")
                .avatar("avatar2.png")
                .dateOfBirth(dob.plusYears(1))
                .location("Location2")
                .bannedUntil(null)
                .roles(Set.of(Role.ROLE_ADMIN))
                .technologyStack(List.of("Python"))
                .linksToMedia(List.of())
                .createdAt(now.plusDays(1).toLocalDate())
                .build();

        when(imageService.buildImageUrl("avatar1.png")).thenReturn("http://image/avatar1.png");
        when(imageService.buildImageUrl("avatar2.png")).thenReturn("http://image/avatar2.png");

        List<UserDto> result = userMapper.mapToDto(List.of(user1, user2));

        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto dto1 = result.get(0);
        assertEquals("1", dto1.getId());
        assertEquals("user1", dto1.getUsername());
        assertEquals("http://image/avatar1.png", dto1.getAvatarUrl());
        assertEquals(dob, dto1.getDateOfBirth());
        assertEquals("Location1", dto1.getLocation());
        assertEquals(now.plusDays(1), dto1.getBannedUntil());
        assertEquals(Set.of(Role.ROLE_USER), dto1.getRoles());
        assertEquals(List.of("Java", "Spring"), dto1.getTechnologyStack());
        assertEquals(List.of("github.com/user1"), dto1.getLinksToMedia());
        assertEquals(now.toLocalDate(), dto1.getCreatedAt());

        UserDto dto2 = result.get(1);
        assertEquals("2", dto2.getId());
        assertEquals("user2", dto2.getUsername());
        assertEquals("http://image/avatar2.png", dto2.getAvatarUrl());
        assertEquals(dob.plusYears(1), dto2.getDateOfBirth());
        assertEquals("Location2", dto2.getLocation());
        assertNull(dto2.getBannedUntil());
        assertEquals(Set.of(Role.ROLE_ADMIN), dto2.getRoles());
        assertEquals(List.of("Python"), dto2.getTechnologyStack());
        assertTrue(dto2.getLinksToMedia().isEmpty());
        assertEquals(now.plusDays(1).toLocalDate(), dto2.getCreatedAt());
    }

    @Test
    void mapToDto_WithSingleUser_ReturnsMappedDto() {
        LocalDate now = LocalDate.now();
        LocalDate dob = LocalDate.of(1985, 5, 15);

        User user = User.builder()
                .id("3")
                .username("testuser")
                .avatar("test.png")
                .dateOfBirth(dob)
                .location("Test Location")
                .bannedUntil(null)
                .roles(Set.of(Role.ROLE_MODERATOR))
                .technologyStack(List.of("Test"))
                .linksToMedia(List.of("test.com"))
                .createdAt(now)
                .build();

        when(imageService.buildImageUrl("test.png")).thenReturn("http://image/test.png");

        UserDto result = userMapper.mapToDto(user);

        assertNotNull(result);
        assertEquals("3", result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("http://image/test.png", result.getAvatarUrl());
        assertEquals(dob, result.getDateOfBirth());
        assertEquals("Test Location", result.getLocation());
        assertNull(result.getBannedUntil());
        assertEquals(Set.of(Role.ROLE_MODERATOR), result.getRoles());
        assertEquals(List.of("Test"), result.getTechnologyStack());
        assertEquals(List.of("test.com"), result.getLinksToMedia());
        assertEquals(now, result.getCreatedAt());
    }

    @Test
    void mapToDto_WithMultipleRoles_ReturnsCorrectRoleSet() {
        User user = User.builder()
                .id("4")
                .username("multirole")
                .avatar("multi.png")
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR))
                .createdAt(LocalDate.now())
                .build();

        when(imageService.buildImageUrl("multi.png")).thenReturn("http://image/multi.png");

        UserDto result = userMapper.mapToDto(user);

        assertNotNull(result);
        assertEquals(Set.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR), result.getRoles());
    }

    @Test
    void mapToDto_WithNullAvatar_ReturnsDtoWithNullAvatarUrl() {
        User user = User.builder()
                .id("5")
                .username("noavatar")
                .avatar(null)
                .roles(Set.of(Role.ROLE_USER))
                .createdAt(LocalDate.now())
                .build();

        when(imageService.buildImageUrl(null)).thenReturn(null);

        UserDto result = userMapper.mapToDto(user);

        assertNotNull(result);
        assertEquals("5", result.getId());
        assertEquals("noavatar", result.getUsername());
        assertNull(result.getAvatarUrl());
        assertEquals(Set.of(Role.ROLE_USER), result.getRoles());
    }

    @Test
    void mapToUpdateRequest_WithUser_ReturnsUpdateRequest() {
        LocalDate dob = LocalDate.of(1995, 10, 20);

        User user = User.builder()
                .username("updateuser")
                .location("Update Location")
                .dateOfBirth(dob)
                .technologyStack(List.of("Update Tech"))
                .linksToMedia(List.of("update.com"))
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                .build();

        UpdateUserRequest result = userMapper.mapToUpdateRequest(user);

        assertNotNull(result);
        assertEquals("updateuser", result.getUsername());
        assertEquals("Update Location", result.getLocation());
        assertEquals(dob, result.getDateOfBirth());
        assertEquals(List.of("Update Tech"), result.getTechnologyStack());
        assertEquals(List.of("update.com"), result.getLinksToMedia());
        assertNull(result.getAvatar());
    }

    @Test
    void mapToUpdateRequest_WithEmptyCollections_ReturnsUpdateRequestWithEmptyCollections() {
        User user = User.builder()
                .username("emptycollections")
                .technologyStack(List.of())
                .linksToMedia(List.of())
                .roles(Set.of())
                .build();

        UpdateUserRequest result = userMapper.mapToUpdateRequest(user);

        assertNotNull(result);
        assertEquals("emptycollections", result.getUsername());
        assertTrue(result.getTechnologyStack().isEmpty());
        assertTrue(result.getLinksToMedia().isEmpty());
    }
}