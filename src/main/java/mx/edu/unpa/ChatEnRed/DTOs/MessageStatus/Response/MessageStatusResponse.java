package mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MessageStatusResponse {
    private Integer id;
    private Integer messageId;
    private Integer recipientId;
    private Boolean delivered;
    private LocalDateTime deliveredAt;
    private Boolean reading;
    private LocalDateTime readAt;
}