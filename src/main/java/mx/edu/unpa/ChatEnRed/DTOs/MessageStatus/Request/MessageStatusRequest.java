package mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MessageStatusRequest {
    private Integer messageId;
    private Integer recipientId;
    private Boolean delivered;
    private LocalDateTime deliveredAt;
    private Boolean reading;
    private LocalDateTime readAt;
}