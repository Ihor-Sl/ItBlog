package ua.iate.itblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.iate.itblog.model.user.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private LocalDateTime bannedUntil;
    private Set<Role> role;
    private List<String> technologyStack;
    private List<String> linksToMedia;
    private LocalDate createdAt;
}
