package mx.edu.unpa.ChatEnRed.DTOs.Session.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SessionResponse {
    private Integer id;
    private Integer userId;
    private String deviceInfo;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}