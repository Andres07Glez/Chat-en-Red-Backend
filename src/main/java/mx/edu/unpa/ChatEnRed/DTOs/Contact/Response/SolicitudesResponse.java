package mx.edu.unpa.ChatEnRed.DTOs.Contact.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudesResponse {
    private Integer id;

    // Datos del "Otro" usuario para mostrar en la lista
    private Integer otherUserId;
    private String otherDisplayName;
    private String otherAvatarUrl;

    // Metadatos de la relación
    private String statusLabel; // Ej: "Pendiente", "Aceptado"
    private String statusCode;  // Ej: "PENDING", "ACCEPTED"
    private LocalDateTime createdAt;
}
