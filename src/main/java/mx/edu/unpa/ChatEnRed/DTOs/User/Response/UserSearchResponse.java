package mx.edu.unpa.ChatEnRed.DTOs.User.Response;

import lombok.Data;

@Data
public class UserSearchResponse {
    private Integer id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Boolean isOnline;

    // Información de contacto (si ya lo tienes agregado)
    private Boolean alreadyContact;      // true/false
    private String contactStatus;        // "PENDING", "ACCEPTED", etc.
    private Integer contactId;           // ID del contacto si existe
}