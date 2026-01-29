package mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConversationResponse {
    private Integer id;
    private Integer conversationTypeId;
    private String title;
    private Integer createdById;
    private LocalDateTime createdAt;
}