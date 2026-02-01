package mx.edu.unpa.ChatEnRed.DTOs.Attachment.Request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttachmentRequest {
	private Integer messageId;
	private String filename;
	private String mimeType;
	private Integer size;
	private String storageUrl;
	private String checksum;
}