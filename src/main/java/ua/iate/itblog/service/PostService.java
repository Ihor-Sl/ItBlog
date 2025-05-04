package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.UpdatePostRequest;
import ua.iate.itblog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("errors.post.id.not-found", id));
    }

    public Post createPost(String userId, CreatePostRequest createPostRequest) {
        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setUserId(userId);
        post.setTitle(createPostRequest.getTitle());
        post.setContent(createPostRequest.getContent());
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public Post updatePost(String id, UpdatePostRequest updatePostRequest) {
        Post post = findById(id);
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public boolean isPostOwner(String postId, String userId) {
        return postRepository.existsByIdAndUserId(postId, userId);
    }
}