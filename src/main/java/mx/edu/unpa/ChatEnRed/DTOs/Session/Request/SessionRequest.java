package mx.edu.unpa.ChatEnRed.DTOs.Session.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SessionRequest {
    private Integer userId;
    private String refreshTokenHash;
    private String deviceInfo;
    private LocalDateTime createdAt; 
    private LocalDateTime expiresAt; 
}