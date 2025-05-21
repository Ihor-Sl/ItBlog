package ua.iate.itblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.repository.post.CustomPostRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String>, CustomPostRepository {
    boolean existsByIdAndUserId(String postId, String userId);

    Page<Post> findAllBy(TextCriteria criteria, Pageable pageable);

    List<Post> findTop10ByOrderByCreatedAtDesc();

    List<Post> findTop10ByOrderByLikedUserIdsDesc();
}