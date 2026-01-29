package mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConversationRequest {
    private Integer conversationTypeId;
    private String title;
    private Integer createdById;
    private LocalDateTime createdAt; 
} 