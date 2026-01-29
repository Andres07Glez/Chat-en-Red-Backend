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

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> findAll() {
        return contactRepository.findAll().stream()
                .map(contactMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactResponse> findById(Integer id) {
        return contactRepository.findById(id)
                .map(contactMapper::toResponse);
    }

    @Override
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
    }
}