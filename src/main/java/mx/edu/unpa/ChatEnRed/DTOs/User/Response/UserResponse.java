package mx.edu.unpa.ChatEnRed.DTOs.User.Response;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Response intentionally excludes passwordHash for security.
 */
@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;
}