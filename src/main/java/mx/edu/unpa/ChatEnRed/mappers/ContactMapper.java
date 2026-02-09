package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "contactUser.id", target = "contactUserId")
    @Mapping(source = "contactStatus.id", target = "contactStatusId")

    // CORRECCIÓN: Usamos 'label' en lugar de 'name'
    @Mapping(source = "contactStatus.label", target = "contactStatusName")

    // Ignoramos estos para llenarlos manualmente abajo
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "contactAvatarUrl", ignore = true)
    ContactResponse toResponse(Contact entity);

    // Mapeo inverso (sin cambios)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "contactUser", target = "contactUser")
    @Mapping(source = "contactStatus", target = "contactStatus")
    @Mapping(target = "updatedAt", ignore = true)
    Contact toEntity(ContactRequest request, User owner, User contactUser, ContactStatus contactStatus);

    // Lógica personalizada (sin cambios)
    @AfterMapping
    default void calcularDatosDePerfil(@MappingTarget ContactResponse dto, Contact entity) {
        if (entity.getContactUser() == null) return;

        User user = entity.getContactUser();

        // 1. Nombre por defecto: username
        String finalName = user.getUsername();

        // 2. Si tiene perfil, buscamos datos bonitos
        if (user.getUserProfile() != null) {
            UserProfile profile = user.getUserProfile();

            if (profile.getDisplayName() != null && !profile.getDisplayName().isEmpty()) {
                finalName = profile.getDisplayName();
            }
            dto.setContactAvatarUrl(profile.getAvatarUrl());
        }

        dto.setContactName(finalName);
    }
}