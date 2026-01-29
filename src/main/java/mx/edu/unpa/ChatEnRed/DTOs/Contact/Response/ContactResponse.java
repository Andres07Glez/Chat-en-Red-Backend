package mx.edu.unpa.ChatEnRed.DTOs.Contact.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ContactResponse {
    private Integer id;
    private Integer ownerId;
    private Integer contactUserId;
    private Integer contactStatusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}