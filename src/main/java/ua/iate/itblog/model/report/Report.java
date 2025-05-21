package ua.iate.itblog.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Report.COLLECTION_NAME)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    public static final String COLLECTION_NAME = "reports";

    private String id;
    private String title;
    private String description;
    @Indexed(unique = false)
    private String postId;
    @Indexed(unique = false)
    private String userId;
    private String reporterId;
}