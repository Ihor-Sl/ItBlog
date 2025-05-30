package ua.iate.itblog.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = User.COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class User implements Serializable {

    public static final String COLLECTION_NAME = "users";

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Indexed(unique = true)
    private String username;

    private Set<Role> roles = new HashSet<>();

    private LocalDateTime bannedUntil;

    private String avatar;

    private LocalDate dateOfBirth;

    private String location;

    private List<String> technologyStack;

    private List<String> linksToMedia;

    private LocalDate createdAt;
}