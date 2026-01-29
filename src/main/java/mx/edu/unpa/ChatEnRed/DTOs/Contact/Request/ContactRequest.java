package mx.edu.unpa.ChatEnRed.DTOs.Contact.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ContactRequest {
    private Integer ownerId;
    private Integer contactUserId;
    private Integer contactStatusId;
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 
}