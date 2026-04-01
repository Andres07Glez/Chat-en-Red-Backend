package mx.edu.unpa.ChatEnRed.DTOs.Contact.Response;

import lombok.Data;

@Data
public class ContactLookupResponse {

    private boolean userExists;
    private boolean isSelf;

    // NONE, PENDING, ACCEPTED, REMOVED
    private String relationStatus;

    // OUTGOING, INCOMING, NONE
    private String relationDirection;

    private Integer contactId;

    // Datos visuales
    private String username;
    private String displayName;
}

