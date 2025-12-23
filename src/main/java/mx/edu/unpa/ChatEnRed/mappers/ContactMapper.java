package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "contactUser.id", target = "contactUserId")
    @Mapping(source = "contactStatus.id", target = "contactStatusId")
    ContactResponse toResponse(Contact entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "contactUser", target = "contactUser")
    @Mapping(source = "contactStatus", target = "contactStatus")
    Contact toEntity(ContactRequest request, User owner, User contactUser, ContactStatus contactStatus);
}