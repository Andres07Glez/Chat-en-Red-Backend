package mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserProfileRequest {
    private Integer userId; 
    private String displayName;
    private String avatarUrl;
    private String bio;
    }