package ua.iate.itblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.service.ImageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ImageService imageService;

    public List<UserDto> mapToDto(List<User> users) {
        return users.stream().map(this::mapToDto).toList();
    }

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(imageService.buildImageUrl(user.getAvatar()))
                .dateOfBirth(user.getDateOfBirth())
                .location(user.getLocation())
                .bannedUntil(user.getBannedUntil())
                .role(user.getRoles())
                .technologyStack(user.getTechnologyStack())
                .linksToMedia(user.getLinksToMedia())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UpdateUserRequest mapToUpdateRequest(User user) {
        return UpdateUserRequest.builder()
                .username(user.getUsername())
                .location(user.getLocation())
                .dateOfBirth(user.getDateOfBirth())
                .technologyStack(user.getTechnologyStack())
                .linksToMedia(user.getLinksToMedia())
                .build();
    }
}
