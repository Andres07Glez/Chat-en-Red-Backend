package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import mx.edu.unpa.ChatEnRed.enums.MessageStatus;

@Data
@Entity
@Table(name="Messages")
public class Message implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
	private int id;
	@Column(name="conversation_id")
	private int conversationId;
	@Column(name="sender_id")
	private int senderId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private MessageStatus status = MessageStatus.TEXT;
	
	
	@Column(name="content")
	private String content;
	@Column(name="created_at")
	private LocalDate createdAt;
	@Column(name="edited_at")
	private LocalDate editedAt;
	
	
}
