package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.user.CreateUserRequest;
import ua.iate.itblog.model.user.UpdateUserRequest;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public List<User> findAll() {
        return userRepository.findAll();
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

    public User updateUser(UpdateUserRequest userRequest, String id) {
        User user = findById(id);
        updateAvatar(user, userRequest.getAvatar());
        userRequest.getTechnologyStack().removeIf(s -> s == null || s.trim().isBlank());
        userRequest.getLinksToMedia().removeIf(s -> s == null || s.trim().isBlank());

        user.setUsername(userRequest.getUsername());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setLocation(userRequest.getLocation());
        user.setTechnologyStack(userRequest.getTechnologyStack());
        user.setLinksToMedia(userRequest.getLinksToMedia());

        return userRepository.save(user);
    }

    private void updateAvatar(User user, MultipartFile file) {
        Optional.ofNullable(user.getAvatar()).ifPresent(imageService::delete);
        user.setAvatar(file.isEmpty() ? null : imageService.upload(file));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UpdateUserRequest mapToUpdateUserRequest(User user) {
        return UpdateUserRequest.builder()
                .avatar(null)
                .username(user.getUsername())
                .location(user.getLocation())
                .dateOfBirth(user.getDateOfBirth())
                .technologyStack(user.getTechnologyStack())
                .linksToMedia(user.getLinksToMedia())
                .build();
    }
}