package mx.edu.unpa.ChatEnRed.DTOs.Conversation;

import java.time.LocalDateTime;

public interface ChatListItemDTO {
    Integer getConversationId();
    String getTitle();          // Nombre del grupo o null si es directo
    String getConversationType(); // 'DIRECT' o 'GROUP'

    // Datos del último mensaje
    String getLastMessageContent();
    String getLastMessageIv();     // Necesario para descifrar el snippet en el frontend
    LocalDateTime getLastMessageTime();
    String getLastMessageSender(); // Para mostrar "Juan: Hola"

    // Metadatos
    Long getUnreadCount();
    Integer getOtherUserId();      // ID del otro usuario (si es chat directo)
    String getOtherUserName();     // Nombre del otro usuario
    String getOtherUserAvatar();   // Avatar del otro usuario
}
