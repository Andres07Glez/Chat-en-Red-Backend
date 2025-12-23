package mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserProfileResponse {
    private Integer userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private LocalDateTime updatedAt;
}