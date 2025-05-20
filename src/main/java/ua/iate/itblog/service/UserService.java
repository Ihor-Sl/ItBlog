package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.dto.UserDto;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.user.*;
import ua.iate.itblog.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("errors.user.id.not-found", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("errors.user.email.not-found", email));
    }

    public Page<User> findAll(UserSearchRequest userSearchRequest, Pageable pageable) {
        return userRepository.findAll(userSearchRequest, pageable);
    }

    public void createUser(CreateUserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .username(userRequest.getUsername())
                .roles(Set.of(Role.ROLE_USER))
                .createdAt(LocalDate.now())
                .build();
        userRepository.save(user);
    }

    public User update(User user, UpdateUserRequest updateUserRequest) {
        MultipartFile file = updateUserRequest.getAvatar();
        Optional.ofNullable(user.getAvatar()).ifPresent(imageService::delete);
        user.setAvatar(file.isEmpty() ? null : imageService.upload(file));
        updateUserRequest.getTechnologyStack().removeIf(StringUtils::isBlank);
        updateUserRequest.getLinksToMedia().removeIf(StringUtils::isBlank);
        user.setUsername(updateUserRequest.getUsername());
        user.setDateOfBirth(updateUserRequest.getDateOfBirth());
        user.setLocation(updateUserRequest.getLocation());
        user.setTechnologyStack(updateUserRequest.getTechnologyStack());
        user.setLinksToMedia(updateUserRequest.getLinksToMedia());
        return userRepository.save(user);
    }

    public void updateRole(String id) {
        User user = findById(id);
        Set<Role> role = user.getRoles();
        Set<Role> updatedRole = updateRole(role);
        user.setRoles(updatedRole);
        userRepository.save(user);
    }

    public Set<Role> updateRole(Set<Role> roles) {
        if (roles.contains(Role.ROLE_MODERATOR)) {
            roles.remove(Role.ROLE_MODERATOR);
        } else {
            roles.add(Role.ROLE_MODERATOR);
        }
        return roles;
    }

    public void updateBannedStatus(UpdateUserBannedRequest updateUserBannedRequest, String id) {
        User user = findById(id);
        user.setBannedUntil(updateUserBannedRequest.getBannedUntil());
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean hasRoleAdmin(User user) {
        return user.getRoles().contains(Role.ROLE_ADMIN);
    }

    public boolean hasRoleForEditBanStatus(String targetId, String whoId) {
        User target = findById(targetId);
        User who = findById(whoId);
        if (who.getRoles().contains(Role.ROLE_ADMIN)) {
            return true;
        }
        return !target.getRoles().contains(Role.ROLE_MODERATOR) &&
                !target.getRoles().contains(Role.ROLE_ADMIN)
                && who.getRoles().contains(Role.ROLE_MODERATOR);
    }

    public boolean userHasBan(UserDto user){
        return user.getBannedUntil() != null;
    }

}