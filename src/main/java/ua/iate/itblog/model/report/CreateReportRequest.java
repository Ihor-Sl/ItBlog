package ua.iate.itblog.model.report;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    @NotNull(message = "{errors.report.title.not-null}")
    @Size(min = 5, max = 50, message = "{errors.report.title.size}")
    private String title;

    @NotNull(message = "{errors.report.description.not-null}")
    @Size(min = 10, max = 500, message = "{errors.report.description.size}")
    private String description;

    private String postId;
    private String UserId;
    private String reporterId;
}