package ua.iate.itblog.repository.post;

import ua.iate.itblog.model.post.projection.PostWithComments;

import java.util.Optional;

public interface CustomPostRepository {
    Optional<PostWithComments> findWithCommentsById(String id);
}
