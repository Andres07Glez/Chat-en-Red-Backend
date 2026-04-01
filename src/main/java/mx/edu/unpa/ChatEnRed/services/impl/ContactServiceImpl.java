/*package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactLookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.ContactMapper;
import mx.edu.unpa.ChatEnRed.repositories.ContactRepository;
import mx.edu.unpa.ChatEnRed.repositories.ContactStatusRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.ContactService;

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

    // ==========================================
    // CONSTANTES (Para evitar errores de dedo)
    // ==========================================
    private static final String CODE_PENDING = "PENDING";
    private static final String CODE_ACCEPTED = "ACCEPTED";
    // OJO: Aquí ponemos el valor real de tu BD ("REMOVED")
    private static final String CODE_REJECTED = "REMOVED";

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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Owner not found with email: " + ownerEmail
                ));

        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contact user not found"
                ));

        ContactStatus status = contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "ContactStatus not found"
                ));

        Contact contact = contactMapper.toEntity(request, owner, contactUser, status);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());

        return Optional.of(contactRepository.save(contact))
                .map(contactMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return this.contactRepository.findById(id)
                .map(contact -> {contactRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<ContactResponse> update(Integer id, ContactRequest request) {

        Contact existing = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contact not found: " + id
                ));

        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contact user not found with id: " + request.getContactUserId()
                ));

        ContactStatus status = contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "ContactStatus not found with id: " + request.getContactStatusId()
                ));

        // 🔒 NO se toca el owner
        existing.setContactUser(contactUser);
        existing.setContactStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());

        return Optional.of(contactRepository.save(existing))
                .map(contactMapper::toResponse);
    }


    //nuevo
    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> findByOwnerUsername(String userName) {
        User owner = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return contactRepository.findByOwner(owner).stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    // =====================================================
    // CASO DE USO: Enviar o reenviar solicitud de contacto
    // =====================================================
    @Override
    @Transactional
    public Optional<ContactResponse> sendOrResendRequest(
            String ownerUsername,
            String targetUsername
    ) {
        // 1. Resolver usuarios
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + ownerUsername));

        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + targetUsername));

        if (owner.getId().equals(target.getId())) {
            throw new IllegalArgumentException("No puedes agregarte a ti mismo");
        }

        // 2. Verificar relación previa (OPTIMIZADO)
        Optional<Contact> existingOpt = contactRepository.findByOwnerAndContactUser(owner, target);

        if (existingOpt.isPresent()) {
            Contact existing = existingOpt.get();
            // Extraemos el código actual para comparar texto vs texto
            String currentStatusCode = existing.getContactStatus().getCode();

            // 🔒 Validaciones rápidas (Sin ir a la BD de Statuses)
            if (CODE_PENDING.equals(currentStatusCode)) {
                throw new IllegalStateException("Ya existe una solicitud pendiente");
            }

            if (CODE_ACCEPTED.equals(currentStatusCode)) {
                throw new IllegalStateException("Este usuario ya es tu contacto");
            }

            // 🔁 Reactivación: Si estaba rechazado/removido
            if (CODE_REJECTED.equals(currentStatusCode)) {
                // AHORA SÍ: Llamamos al helper porque necesitamos el objeto para guardar
                existing.setContactStatus(getPendingStatus());
                existing.setUpdatedAt(LocalDateTime.now());

                return Optional.of(contactRepository.save(existing))
                        .map(contactMapper::toResponse);
            }
        }

        // 3. Crear Nueva Relación (Si no existía ninguna)
        // AHORA SÍ: Llamamos al helper
        Contact newContact = Contact.create(
                owner,
                target,
                getPendingStatus()
        );
        // Asegura fechas si tu método estático create no lo hace
        if (newContact.getCreatedAt() == null) newContact.setCreatedAt(LocalDateTime.now());
        newContact.setUpdatedAt(LocalDateTime.now());

        return Optional.of(contactRepository.save(newContact))
                .map(contactMapper::toResponse);
    }

    // ===============================
    // Métodos Helpers (Se quedan para mantener orden)
    // ===============================
    private ContactStatus getPendingStatus() {
        return contactStatusRepository.findByCode(CODE_PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Status " + CODE_PENDING + " not found"));
    }

    // Estos puedes dejarlos por si los usas en otros métodos futuros (ej. aceptar solicitud)
    private ContactStatus getAcceptedStatus() {
        return contactStatusRepository.findByCode(CODE_ACCEPTED)
                .orElseThrow(() -> new EntityNotFoundException("Status " + CODE_ACCEPTED + " not found"));
    }

    private ContactStatus getRejectedStatus() {
        return contactStatusRepository.findByCode(CODE_REJECTED)
                .orElseThrow(() -> new EntityNotFoundException("Status " + CODE_REJECTED + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactLookupResponse> lookupContactRelation(
            String ownerUsername,
            String targetUsername
    ) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Optional<User> targetOpt = userRepository.findByUsername(targetUsername);

        // 🔴 El usuario buscado no existe
        if (targetOpt.isEmpty()) {
            ContactLookupResponse resp = new ContactLookupResponse();
            resp.setUserExists(false);
            resp.setRelationStatus("NONE");
            return Optional.of(resp);
        }

        User target = targetOpt.get();

        Optional<Contact> contactOpt =
                contactRepository.findByOwnerAndContactUser(owner, target);

        ContactLookupResponse resp = new ContactLookupResponse();
        resp.setUserExists(true);

        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            resp.setRelationStatus(contact.getContactStatus().getCode());
            resp.setContactId(contact.getId());
        } else {
            resp.setRelationStatus("NONE");
        }

        return Optional.of(resp);
    }

}*/

package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactLookupResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.domains.ContactStatus;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.ContactMapper;
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
