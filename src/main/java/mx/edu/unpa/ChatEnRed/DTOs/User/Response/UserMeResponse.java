package mx.edu.unpa.ChatEnRed.DTOs.User.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMeResponse {
    private Integer id;
    private String username;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Campos de user_profiles
    private String displayName;
    private String bio;
    private String avatarUrl;
}