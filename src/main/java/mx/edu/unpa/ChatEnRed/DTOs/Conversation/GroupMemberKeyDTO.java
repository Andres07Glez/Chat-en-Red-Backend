package mx.edu.unpa.ChatEnRed.DTOs.Conversation;

import lombok.Data;

@Data
public class GroupMemberKeyDTO {
    private Integer userId;       // Para quién es esta llave
    private String encryptedKey;  // La llave del grupo cifrada para él
    private String iv;            // El IV usado
}