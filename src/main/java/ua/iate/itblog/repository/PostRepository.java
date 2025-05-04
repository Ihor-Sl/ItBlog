package ua.iate.itblog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.iate.itblog.model.post.Post;

public interface PostRepository extends MongoRepository<Post, String> {
    boolean existsByIdAndUserId(String postId, String userId);
}