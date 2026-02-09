package mx.edu.unpa.ChatEnRed.DTOs.Contact.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ContactResponse {

    // ID del registro en la tabla 'contacts'
    private Integer id;

    // --- IDs de Referencia (Útiles para lógica interna) ---
    private Integer ownerId;
    private Integer contactUserId;
    private Integer contactStatusId;

    // --- Datos Visuales (Poblados por ContactMapper) ---
    // El nombre a mostrar (Username o DisplayName del perfil)
    private String contactName;

    // La etiqueta legible del estado (ej: "En línea", "Ocupado") viene de 'contactStatus.label'
    private String contactStatusName;

    // La URL de la foto de perfil (viene de UserProfile)
    private String contactAvatarUrl;

    // --- Metadatos ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}