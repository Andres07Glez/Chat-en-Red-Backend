package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /*private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_REJECTED = "REJECTED";*/

    // ==========================================
    // CONSTANTES (Para evitar errores de dedo)
    // ==========================================
    private static final String CODE_PENDING = "PENDING";
    private static final String CODE_ACCEPTED = "ACCEPTED";
    // OJO: Aquí ponemos el valor real de tu BD ("REMOVED")
    private static final String CODE_REJECTED = "REMOVED";


    /*@Override
    @Transactional(readOnly = true)
    public List<ContactResponse> findAll() {
        return contactRepository.findAll().stream()
                .map(contactMapper::toResponse)
                .collect(Collectors.toList());
    }*/

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactResponse> findById(Integer id) {
        return contactRepository.findById(id)
                .map(contactMapper::toResponse);
    }

    /*@Override
    @Transactional
    public Optional<ContactResponse> save(ContactRequest request) {
        User owner = this.userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        User contactUser = this.userRepository.findById(request.getContactUserId())
                .orElseThrow(() -> new EntityNotFoundException("Contact user not found with id: " + request.getContactUserId()));

        ContactStatus status = this.contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() -> new EntityNotFoundException("ContactStatus not found with id: " + request.getContactStatusId()));

        Contact contact = contactMapper.toEntity(request, owner, contactUser, status);

        // Preferir que el servidor asigne timestamps si no los proporciona el cliente
        if (contact.getCreatedAt() == null) {
            contact.setCreatedAt(LocalDateTime.now());
        }
        if (contact.getUpdatedAt() == null) {
            contact.setUpdatedAt(contact.getCreatedAt());
        }

        return Optional.of(contact)
        		.map(contactRepository::save)
        		.map(contactMapper::toResponse);
    }*/

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

    /*@Override
    @Transactional
    public Optional<ContactResponse> update(Integer id, ContactRequest request) {
        Contact existing = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found: " + id));

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() -> new EntityNotFoundException("Contact user not found with id: " + request.getContactUserId()));

        ContactStatus status = contactStatusRepository.findById(request.getContactStatusId())
                .orElseThrow(() -> new EntityNotFoundException("ContactStatus not found with id: " + request.getContactStatusId()));

        existing.setOwner(owner);
        existing.setContactUser(contactUser);
        existing.setContactStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        // if (request.getCreatedAt() != null) existing.setCreatedAt(request.getCreatedAt());

        return Optional.of(existing)
        		.map(contactRepository::save)
        		.map(contactMapper::toResponse);
    }*/

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
}