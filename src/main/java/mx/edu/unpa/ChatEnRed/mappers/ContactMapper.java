package mx.edu.unpa.ChatEnRed.mappers;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

// componentModel = "spring" permite inyectarlo con @Autowired o constructores
@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface ContactMapper {

    // ---------------------------------------------------------
    // 1. Método MÁGICO para crear el Espejo
    // ---------------------------------------------------------
    @Mapping(target = "id", ignore = true) // Nuevo ID autogenerado
    @Mapping(source = "contactUser", target = "owner") // Intercambiamos: El que recibió ahora es dueño
    @Mapping(source = "owner", target = "contactUser") // Intercambiamos: El dueño ahora es contacto
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())") // Fecha nueva
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contactStatus", source = "contactStatus") // Mantiene el status (ACCEPTED)
    Contact createMirror(Contact originalRequest);


    // ---------------------------------------------------------
    // 2. Tu método toResponse (Lo mantenemos como default)
    // ---------------------------------------------------------
    // Como MapStruct es una interfaz, para lógica compleja usamos 'default'
    default ContactResponse toResponse(Contact contact, Integer currentUserId) {
        if (contact == null) return null;

        User otherUser = contact.getOwner().getId().equals(currentUserId)
                ? contact.getContactUser()
                : contact.getOwner();

        return ContactResponse.builder()
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