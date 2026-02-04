package mx.edu.unpa.ChatEnRed.DTOs.Message.Response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageResponse {
	private Integer id;
	private Integer conversationId;
	private Integer senderId;
	// AGREGADO: Necesario para mostrar "Maria" encima de la burbuja
	private String senderName;
	private String messageTypeCode;
	// AGREGADO: Fundamental para el CSS (true = derecha, false = izquierda)
	@JsonProperty("isMine")
	private boolean mine;
	private String content; // Aquí viajará el Cifrado (Base64)
	private String iv;      // Aquí viajará el IV (Base64)

	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
}


