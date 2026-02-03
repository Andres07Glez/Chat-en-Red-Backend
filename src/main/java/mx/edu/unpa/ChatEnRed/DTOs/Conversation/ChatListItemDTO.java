package mx.edu.unpa.ChatEnRed.DTOs.Conversation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatListItemDTO {
    private Integer id;             // ID de la conversación
    private String name;            // Título calculado (Nombre de grupo o del otro usuario)
    private String lastMessage;     // "Hola..."
    private LocalDateTime lastActivity; // Para ordenar (lastMessageAt)
    private Boolean isGroup;        // Para que Angular sepa qué ícono poner
    private Integer unreadCount;    // (Opcional, lo dejamos en 0 por ahora)
}
