package ua.iate.itblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String location;
    private List<String> technologyStack;
    private List<String> linksToMedia;
    private LocalDate createdAt;
}
