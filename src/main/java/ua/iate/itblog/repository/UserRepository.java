package ua.iate.itblog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.iate.itblog.model.user.User;
import ua.iate.itblog.repository.user.CustomUserRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, CustomUserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}