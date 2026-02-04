package mx.edu.unpa.ChatEnRed.DTOs.Message.Request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageRequest {
	private Integer conversationId;
	private String content;         // Texto (o Ciphertext Base64)
	private String messageTypeCode; // "TEXT", "FILE", etc.
	private String iv;              // Vector de inicialización (Para AES/GCM)

	// Opcionales, generalmente se llenan en el backend/DB
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
}
