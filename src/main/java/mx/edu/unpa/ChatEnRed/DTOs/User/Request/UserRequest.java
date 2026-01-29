package mx.edu.unpa.ChatEnRed.DTOs.User.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String passwordHash;
    private Boolean isActive;
    private LocalDateTime createdAt; // opcional: el servidor puede asignarlo si es null
    private LocalDateTime lastSeen;  // opcional
}