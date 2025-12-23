package mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConversationMemberRequest {
    private Integer conversationId;
    private Integer userId;
    private Integer roleStatusId;
    private LocalDateTime joinedAt; 
}