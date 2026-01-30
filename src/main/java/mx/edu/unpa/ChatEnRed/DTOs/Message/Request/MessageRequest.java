package mx.edu.unpa.ChatEnRed.DTOs.Message.Request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageRequest {
	
	private Integer conversationId;
	private Integer senderId;
	//private String senderUsername; // opcional
	private String messageTypeCode;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	private String iv;

}
