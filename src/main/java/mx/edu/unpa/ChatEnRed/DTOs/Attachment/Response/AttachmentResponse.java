package mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttachmentResponse {
    private Integer id;
    private Integer messageId;
    private String filename;
    private String mimeType;
    private Integer size;
    private String storageUrl;
    private String checksum;
    private LocalDateTime createdAt;
}
