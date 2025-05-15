package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.model.user.CreateUserRequest;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.model.user.UserSearchRequest;
import ua.iate.itblog.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

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

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}