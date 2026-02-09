package mx.edu.unpa.ChatEnRed.services.impl;

import lombok.RequiredArgsConstructor;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;
import mx.edu.unpa.ChatEnRed.mappers.ContactMapper;
import mx.edu.unpa.ChatEnRed.repositories.ContactRepository;
import mx.edu.unpa.ChatEnRed.repositories.ContactStatusRepository;
import mx.edu.unpa.ChatEnRed.services.ContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactStatusRepository statusRepository;
    private final ContactMapper contactMapper;
    private final ContactStatusRepository contactStatusRepository;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACCEPTED = "ACCEPTED";

    @Override
    public List<ContactResponse> findAll() {
        // Implementación básica para que no marque error
        return contactRepository.findAll().stream()
                .map(contact -> contactMapper.toResponse(contact, contact.getOwner().getId()))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> getSentRequests(Integer userId) {
        return contactRepository.findByOwnerIdAndContactStatusCode(userId, STATUS_PENDING)
                .stream()
                .map(contact -> contactMapper.toResponse(contact, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> getReceivedRequests(Integer userId) {
        return contactRepository.findByContactUserIdAndContactStatusCode(userId, STATUS_PENDING)
                .stream()
                .map(contact -> contactMapper.toResponse(contact, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContactResponse acceptRequest(Integer contactId, Integer currentUserId) {
        Contact originalRequest = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        // Validaciones...
        if (!originalRequest.getContactUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("No puedes aceptar una solicitud ajena");
        }

        ContactStatus acceptedStatus = statusRepository.findByCode("ACCEPTED")
                .orElseThrow(() -> new RuntimeException("Estado ACCEPTED no encontrado"));

        // 1. Actualizamos la original
        originalRequest.setContactStatus(acceptedStatus);
        contactRepository.save(originalRequest);

        // 2. CREAMOS EL ESPEJO USANDO EL MAPPER
        boolean existsReverse = contactRepository.existsByOwnerIdAndContactUserId(
                currentUserId,
                originalRequest.getOwner().getId()
        );

        if (!existsReverse) {
            // ¡Aquí ocurre la magia! Una sola línea:
            Contact mirrorContact = contactMapper.createMirror(originalRequest);

            contactRepository.save(mirrorContact);
        }

        return contactMapper.toResponse(originalRequest, currentUserId);
    }

    @Override
    @Transactional
    public void rejectOrDeleteRequest(Integer contactId, Integer currentUserId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Registro no encontrado"));

        if (contact.getOwner().getId().equals(currentUserId) ||
                contact.getContactUser().getId().equals(currentUserId)) {
            contactRepository.delete(contact);
        } else {
            throw new RuntimeException("No tienes permiso para realizar esta acción");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactResponse> findById(Integer id) {
        return contactRepository.findById(id)
                .map(contact -> contactMapper.toResponse(contact, contact.getOwner().getId()));
    }

    @Override
    @Transactional
    public Optional<ContactResponse> save(ContactRequest request) {
        // Aquí iría la lógica para enviar una nueva solicitud
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            return Optional.of(true);
        }
        return Optional.of(false);
    }

    @Override
    @Transactional
    public Optional<ContactResponse> update(Integer id, ContactRequest request) {
        return Optional.empty();
    }

}