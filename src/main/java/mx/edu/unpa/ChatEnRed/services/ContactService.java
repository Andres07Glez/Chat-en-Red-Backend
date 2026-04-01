package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactLookupResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;

public interface ContactService {
    //List<ContactResponse> findAll();
    Optional<ContactResponse> findById(Integer id);
    //Se agrego owner email
    Optional<ContactResponse> save(ContactRequest request, String ownerEmail);
    Optional<Boolean> deleteById(Integer id);
    Optional<ContactResponse> update(Integer id, ContactRequest request);
    //nuevo
    List<ContactResponse> findByOwnerUsername(String userName);

    // =====================================================
    // Enviar o reenviar solicitud de contacto
    // =====================================================
    Optional<ContactResponse> sendOrResendRequest(
            String requesterUsername,
            String targetUsername
    );

    Optional<ContactLookupResponse> lookupContactRelation(
            String ownerUsername,
            String targetUsername
    );



}