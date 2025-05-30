package ua.iate.itblog.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = Post.COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class Post {

    public static final String COLLECTION_NAME = "posts";

    @Id
    private String id;
    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;
    @TextIndexed
    private String title;
    @TextIndexed
    private String content;
    private String image;
    private LocalDateTime createdAt;
    private Set<String> likedUserIds;
    private Set<String> dislikedUserIds;
}