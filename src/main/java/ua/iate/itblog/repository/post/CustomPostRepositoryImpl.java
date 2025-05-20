package ua.iate.itblog.repository.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import ua.iate.itblog.model.comment.Comment;
import ua.iate.itblog.model.comment.projection.CommentWithUser;
import ua.iate.itblog.model.post.Post;
import ua.iate.itblog.model.post.projection.PostWithComments;
import ua.iate.itblog.model.user.User;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private static final String _ID = "_id";
    public static final String USER = "user";

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<PostWithComments> findWithCommentsById(String id) {

        MatchOperation match = Aggregation.match(Criteria.where(_ID).is(id));

        LookupOperation lookup = LookupOperation.newLookup()
                .from(Comment.COLLECTION_NAME)
                .localField(_ID)
                .foreignField(Comment.Fields.postId)
                .pipeline(
                        Aggregation.lookup(
                                User.COLLECTION_NAME,
                                PostWithComments.Fields.userId,
                                _ID,
                                USER
                        ),
                        Aggregation.unwind(USER),
                        Aggregation.project(
                                        CommentWithUser.Fields.postId,
                                        CommentWithUser.Fields.userId,
                                        CommentWithUser.Fields.content,
                                        CommentWithUser.Fields.createdAt,
                                        CommentWithUser.Fields.updatedAt
                                )
                                .andExpression(_ID).as(PostWithComments.Fields.id)
                                .andExpression(USER + "." + User.Fields.username).as(CommentWithUser.Fields.username)
                                .andExpression(USER + "." + User.Fields.avatar).as(CommentWithUser.Fields.avatar)
                )
                .as(PostWithComments.Fields.comments);

        Aggregation aggregation = Aggregation.newAggregation(match, lookup);

        return Optional.ofNullable(mongoTemplate.aggregate(aggregation, Post.class, PostWithComments.class)
                .getUniqueMappedResult());
    }
}
