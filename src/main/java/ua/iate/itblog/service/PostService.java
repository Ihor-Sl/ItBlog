package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.comment.AddCommentRequest;
import ua.iate.itblog.model.comment.Comment;
import ua.iate.itblog.model.post.CreatePostRequest;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.repository.CommentRepository;
import ua.iate.itblog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Page<Post> findAll(String search, Pageable pageable) {
        if (StringUtils.isNotBlank(search)) {
            return postRepository.findAllBy(TextCriteria.forDefaultLanguage().matchingAny(search), pageable);
        }
        return postRepository.findAll(pageable);
    }

    public Post findById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("errors.post.id.not-found", id));
    }

    public PostWithComments findWithCommentsById(String id) {
        return postRepository.findWithCommentsById(id)
                .orElseThrow(() -> new NotFoundException("errors.post.id.not-found", id));
    }

    public Post createPost(String userId, CreatePostRequest createPostRequest) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(createPostRequest.getTitle());
        post.setContent(createPostRequest.getContent());
        MultipartFile file = createPostRequest.getImage();
        post.setImage(file.isEmpty() ? null : imageService.upload(file));
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void delete(Post post) {
        Optional.ofNullable(post.getImage()).ifPresent(imageService::delete);
        postRepository.delete(post);
        commentRepository.deleteAllByPostId(post.getId());
    }

    public boolean isPostOwner(String postId, String userId) {
        return postRepository.existsByIdAndUserId(postId, userId);
    }

    public boolean isCommentOwner(String commentId, String userId) {
        return commentRepository.existsByIdAndUserId(commentId, userId);
    }

    public Comment addComment(String postId, String userId, AddCommentRequest addCommentRequest) {
        return commentRepository.save(Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(addCommentRequest.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }

    public void like(String postId, String currentUserId) {
        Post post = findById(postId);

        Set<String> likedUserIds = Optional.ofNullable(post.getLikedUserIds())
                .orElseGet(() -> {
                    Set<String> newSet = new HashSet<>();
                    post.setLikedUserIds(newSet);
                    return newSet;
                });

        if (!likedUserIds.add(currentUserId)) {
            likedUserIds.remove(currentUserId);
        }

        postRepository.save(post);
    }

    public void dislike(String postId, String currentUserId) {
        Post post = findById(postId);

        Set<String> likedUserIds = Optional.ofNullable(post.getDislikedUserIds())
                .orElseGet(() -> {
                    Set<String> newSet = new HashSet<>();
                    post.setDislikedUserIds(newSet);
                    return newSet;
                });

        if (!likedUserIds.add(currentUserId)) {
            likedUserIds.remove(currentUserId);
        }

        postRepository.save(post);
    }

    public List<Post> findTop10ByOrderByCreatedAtDesc() {
        return postRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Post> findTop10ByOrderByLikedUserIdsDesc() {
        return postRepository.findTop10ByOrderByLikedUserIdsDesc();
    }
}