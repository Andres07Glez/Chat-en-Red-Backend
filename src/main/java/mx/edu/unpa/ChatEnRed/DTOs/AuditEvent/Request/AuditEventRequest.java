package mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuditEventRequest {
    private Integer userId;
    private String eventType;
    private String eventData;
    private LocalDateTime createdAt;
}