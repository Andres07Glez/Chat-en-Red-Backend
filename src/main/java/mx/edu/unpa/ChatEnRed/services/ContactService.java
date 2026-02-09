package mx.edu.unpa.ChatEnRed.services;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import java.util.List;
import java.util.Optional;

public interface ContactService {
    // --- Métodos originales (mantenidos para compatibilidad) ---
    List<ContactResponse> findAll();
    Optional<ContactResponse> findById(Integer id);
    Optional<ContactResponse> save(ContactRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<ContactResponse> update(Integer id, ContactRequest request);

    // --- Métodos NUEVOS para tu vista de Solicitudes ---
    List<ContactResponse> getSentRequests(Integer userId);
    List<ContactResponse> getReceivedRequests(Integer userId);

    // Acciones de la vista (Aceptar / Rechazar o Cancelar)
    ContactResponse acceptRequest(Integer contactId, Integer currentUserId);
    void rejectOrDeleteRequest(Integer contactId, Integer currentUserId);
}