// ContactRequest.java (CORREGIDO)
package mx.edu.unpa.ChatEnRed.DTOs.Contact.Request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ContactRequest {
    @NotNull(message = "El ID del usuario contacto es requerido")
    private Integer contactUserId;
    // NO incluir ownerId - se obtiene del usuario autenticado
    // NO incluir contactStatusId - siempre empieza como PENDING
    // NO incluir fechas - el servidor las genera
}