
package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactLookupResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.SolicitudesResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.ContactMapper;
import mx.edu.unpa.ChatEnRed.mappers.SolicitudMapper;
import mx.edu.unpa.ChatEnRed.repositories.ContactRepository;
import mx.edu.unpa.ChatEnRed.repositories.ContactStatusRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.ContactService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactStatusRepository contactStatusRepository;

    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private SolicitudMapper solicitudMapper;

    // ==========================================
    // CONSTANTES
    // ==========================================
    private static final String CODE_PENDING  = "PENDING";
    private static final String CODE_ACCEPTED = "ACCEPTED";
    private static final String CODE_REJECTED = "REMOVED";

    // ==========================================
    // CRUD BÁSICO
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactResponse> findById(Integer id) {
        return contactRepository.findById(id)
                .map(contactMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<ContactResponse> save(ContactRequest request, String ownerEmail) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException("Owner not found with email: " + ownerEmail));

        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Contact user not found"));

        ContactStatus status = contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() ->
                        new EntityNotFoundException("ContactStatus not found"));

        Contact contact = contactMapper.toEntity(request, owner, contactUser, status);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());

        return Optional.of(contactRepository.save(contact))
                .map(contactMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return contactRepository.findById(id)
                .map(contact -> {
                    contactRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<ContactResponse> update(Integer id, ContactRequest request) {

        Contact existing = contactRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Contact not found: " + id));

        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Contact user not found"));

        ContactStatus status = contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() ->
                        new EntityNotFoundException("ContactStatus not found"));

        existing.setContactUser(contactUser);
        existing.setContactStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());

        return Optional.of(contactRepository.save(existing))
                .map(contactMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> findByOwnerUsername(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return contactRepository.findByOwner(owner)
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    // =====================================================
    // ENVIAR / REENVIAR SOLICITUD
    // =====================================================
    @Override
    @Transactional
    public Optional<ContactResponse> sendOrResendRequest(
            String ownerUsername,
            String targetUsername
    ) {

        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (owner.getId().equals(target.getId())) {
            throw new IllegalArgumentException("No puedes agregarte a ti mismo");
        }

        Optional<Contact> existingOpt =
                contactRepository.findByOwnerAndContactUser(owner, target);

        if (existingOpt.isPresent()) {
            Contact existing = existingOpt.get();
            String statusCode = existing.getContactStatus().getCode();

            if (CODE_PENDING.equals(statusCode)) {
                throw new IllegalStateException("Ya existe una solicitud pendiente");
            }

            if (CODE_ACCEPTED.equals(statusCode)) {
                throw new IllegalStateException("Este usuario ya es tu contacto");
            }

            if (CODE_REJECTED.equals(statusCode)) {
                existing.setContactStatus(getPendingStatus());
                existing.setUpdatedAt(LocalDateTime.now());
                return Optional.of(contactRepository.save(existing))
                        .map(contactMapper::toResponse);
            }
        }

        Contact newContact = Contact.create(
                owner,
                target,
                getPendingStatus()
        );

        return Optional.of(contactRepository.save(newContact))
                .map(contactMapper::toResponse);
    }

    // =====================================================
    // LOOKUP DE RELACIÓN (CLAVE PARA EL FRONT)
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactLookupResponse> lookupContactRelation(
            String ownerUsername,
            String targetUsername
    ) {

        ContactLookupResponse resp = new ContactLookupResponse();

        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Optional<User> targetOpt = userRepository.findByUsername(targetUsername);

        // Usuario no existe
        if (targetOpt.isEmpty()) {
            resp.setUserExists(false);
            resp.setRelationStatus("NONE");
            return Optional.of(resp);
        }

        User target = targetOpt.get();
        resp.setUserExists(true);

        // Es el mismo usuario
        if (owner.getId().equals(target.getId())) {
            resp.setRelationStatus("SELF");
            return Optional.of(resp);
        }

        // A → B (yo envié solicitud)
        Optional<Contact> outgoing =
                contactRepository.findByOwnerAndContactUser(owner, target);

        if (outgoing.isPresent()) {
            resp.setRelationStatus(outgoing.get().getContactStatus().getCode());
            resp.setContactId(outgoing.get().getId());
            return Optional.of(resp);
        }

        // B → A (me enviaron solicitud)
        Optional<Contact> incoming =
                contactRepository.findByContactUserAndOwner(owner, target);

        if (incoming.isPresent()) {
            resp.setRelationStatus("INCOMING_" + incoming.get().getContactStatus().getCode());
            resp.setContactId(incoming.get().getId());
            return Optional.of(resp);
        }

        // No hay relación
        resp.setRelationStatus("NONE");
        return Optional.of(resp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudesResponse> getSentRequests(Integer userId) {
        return contactRepository.findByOwnerIdAndContactStatusCode(userId, CODE_PENDING)
                .stream()
                .map(contact -> solicitudMapper.toResponse(contact, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudesResponse> getReceivedRequests(Integer userId) {
        return contactRepository.findByContactUserIdAndContactStatusCode(userId, CODE_PENDING)
                .stream()
                .map(contact -> solicitudMapper.toResponse(contact, userId))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public SolicitudesResponse acceptRequest(Integer contactId, Integer currentUserId) {
        Contact originalRequest = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        // Validaciones...
        if (!originalRequest.getContactUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("No puedes aceptar una solicitud ajena");
        }

        ContactStatus acceptedStatus = contactStatusRepository.findByCode("ACCEPTED")
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
            Contact mirrorContact = solicitudMapper.createMirror(originalRequest);

            contactRepository.save(mirrorContact);
        }

        return solicitudMapper.toResponse(originalRequest, currentUserId);

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

    // =====================================================
    // HELPERS DE STATUS
    // =====================================================
    private ContactStatus getPendingStatus() {
        return contactStatusRepository.findByCode(CODE_PENDING)
                .orElseThrow(() ->
                        new EntityNotFoundException("Status PENDING not found"));
    }

    private ContactStatus getAcceptedStatus() {
        return contactStatusRepository.findByCode(CODE_ACCEPTED)
                .orElseThrow(() ->
                        new EntityNotFoundException("Status ACCEPTED not found"));
    }

    private ContactStatus getRejectedStatus() {
        return contactStatusRepository.findByCode(CODE_REJECTED)
                .orElseThrow(() ->
                        new EntityNotFoundException("Status REMOVED not found"));
    }
}
