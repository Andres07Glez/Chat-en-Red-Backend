package mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuditEventResponse {
    private Integer id;
    private Integer userId;
    private String eventType;
    private String eventData;
    private LocalDateTime createdAt;
}