package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.iate.itblog.exception.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("errors.user.id.not-found", id));
    }

    public User updateUser(UpdateUserRequest userRequest, String id) {
        User user = findById(id);
        user.setUsername(userRequest.getUsername());
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("errors.user.email.not-found", email));
    }
}