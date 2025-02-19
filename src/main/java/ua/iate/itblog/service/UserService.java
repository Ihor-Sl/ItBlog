package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.iate.itblog.model.CreateUserRequest;
import ua.iate.itblog.model.UpdateUserRequest;
import ua.iate.itblog.model.User;
import ua.iate.itblog.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void createUser(CreateUserRequest userRequest) {
        User user = mapToUser(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private User mapToUser(CreateUserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setUsername(userRequest.getUsername());
        user.setCreatedAt(userRequest.getCreatedAt());
        return user;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));
    }

    public User updateUser(UpdateUserRequest userRequest, String id) {
        User user = findById(id);
        if (userRequest.getAvatar() != null) {
            user.setAvatar(userRequest.getAvatar().getName());
        }
        if (userRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userRequest.getDateOfBirth());
        }
        if (userRequest.getLocation() != null) {
            user.setLocation(userRequest.getLocation());
        }
        if (userRequest.getTechnologyStack() != null) {
            user.setTechnologyStack(userRequest.getTechnologyStack());
        }
        if (userRequest.getLinksToMedia() != null) {
            user.setLinksToMedia(userRequest.getLinksToMedia());
        }
        user.setUsername(userRequest.getUsername());
        return userRepository.save(user);
    }

    public boolean checkTechnologyStack(List<String> technologyStack) {
        if (technologyStack == null || technologyStack.isEmpty()) {
            return false;
        }
        technologyStack.removeIf(s -> s == null || s.trim().isEmpty());
        return !technologyStack.isEmpty();
    }

    public boolean checkListLinksToMedia(List<String> linksToMedia) {
        if (linksToMedia == null || linksToMedia.isEmpty()) {
            return false;
        }
        linksToMedia.removeIf(s -> s == null || s.trim().isEmpty());
        return !linksToMedia.isEmpty();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));
    }
}