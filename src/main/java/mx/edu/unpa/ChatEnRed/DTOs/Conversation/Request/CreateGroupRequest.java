package mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request;

import lombok.Data;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.GroupMemberKeyDTO;

import java.util.List;

@Data
public class CreateGroupRequest {
    private String title;
    // Lista de miembros y sus llaves (INCLUYENDO AL CREADOR)
    private List<GroupMemberKeyDTO> members;
}
