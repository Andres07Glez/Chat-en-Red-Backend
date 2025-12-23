package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;

public interface ContactService {
    List<ContactResponse> findAll();
    Optional<ContactResponse> findById(Integer id);
    Optional<ContactResponse> save(ContactRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<ContactResponse> update(Integer id, ContactRequest request);
}