package ua.iate.itblog.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.iate.itblog.model.user.UserSearchRequest;
import ua.iate.itblog.model.user.User;

public interface CustomUserRepository {

    Page<User> findAll(UserSearchRequest userSearchRequest, Pageable pageable);
}
