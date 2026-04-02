package mx.edu.unpa.ChatEnRed.mappers;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.SolicitudesResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {
    // ---------------------------------------------------------
    // 1.  para crear el Espejo
    // ---------------------------------------------------------
    @Mapping(target = "id", ignore = true) // Nuevo ID autogenerado
    @Mapping(source = "contactUser", target = "owner") // Intercambiamos: El que recibió ahora es dueño
    @Mapping(source = "owner", target = "contactUser") // Intercambiamos: El dueño ahora es contacto
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())") // Fecha nueva
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contactStatus", source = "contactStatus") // Mantiene el status (ACCEPTED)
    Contact createMirror(Contact originalRequest);


    // ---------------------------------------------------------
    // 2. toResponse (Lo mantenemos como default)
    // ---------------------------------------------------------
    // Como MapStruct es una interfaz, para lógica compleja usamos 'default'
    default SolicitudesResponse toResponse(Contact contact, Integer currentUserId) {
        if (contact == null) return null;

        User otherUser = contact.getOwner().getId().equals(currentUserId)
                ? contact.getContactUser()
                : contact.getOwner();

        return SolicitudesResponse.builder()
                .id(contact.getId())
                .otherUserId(otherUser.getId())
                .otherDisplayName(otherUser.getProfile() != null ?
                        otherUser.getProfile().getDisplayName() : otherUser.getUsername())
                .otherAvatarUrl(otherUser.getProfile() != null ?
                        otherUser.getProfile().getAvatarUrl() : null)
                .statusLabel(contact.getContactStatus().getLabel())
                .statusCode(contact.getContactStatus().getCode())
                .createdAt(contact.getCreatedAt())
                .build();
    }

}
