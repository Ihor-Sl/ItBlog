package ua.iate.itblog.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.iate.itblog.model.User;
import ua.iate.itblog.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }
}
