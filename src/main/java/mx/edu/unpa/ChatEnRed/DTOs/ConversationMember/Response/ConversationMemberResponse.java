package mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConversationMemberResponse {
    private Integer id;
    private Integer conversationId;
    private Integer userId;
    private Integer roleStatusId;
    private LocalDateTime joinedAt;
}