package ua.iate.itblog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.iate.itblog.model.comment.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
    boolean existsByIdAndUserId(String commentId, String userId);
}

