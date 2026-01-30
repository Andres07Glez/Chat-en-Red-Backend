package mx.edu.unpa.ChatEnRed.DTOs.Message.Response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageResponse {
	private Integer id;
	private Integer conversationId;
	private Integer senderId;
	//private String senderUsername; // opcional
	private String messageTypeCode;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	private String iv;
 // getters/setters, constructor, builder o Lombok @Data
}


