package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response.AttachmentResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.services.ContactService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    /*@GetMapping(path= "/app")
	ResponseEntity<List<ContactResponse>> findAll() {
		return Optional
        		.of(this.contactService.findAll())
        		.map(ResponseEntity::ok)
        		.orElseGet(ResponseEntity.notFound()::build);
        }*/

    @GetMapping("/my")
    public ResponseEntity<List<ContactResponse>> myContacts(Authentication auth) {
        return ResponseEntity.ok(
                contactService.findByOwnerUsername(auth.getName())
        );
    }


    @GetMapping("/fnd")
    public ResponseEntity<ContactResponse> findById(@RequestParam("id") Integer contactId) {
        return contactService.findById(contactId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    /*@PostMapping("/create")
    public ResponseEntity<ContactResponse> save(@RequestBody ContactRequest request) {
        return contactService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }*/

    @PostMapping("/create")
    public ResponseEntity<ContactResponse> save(
            @RequestBody ContactRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return contactService.save(request, email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }


    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer contactId) {
        return contactService.deleteById(contactId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ContactResponse> update(
            @PathVariable("id") Integer contactId,
            @RequestBody ContactRequest request) {
        return contactService.update(contactId, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // =====================================================
    // Enviar o reenviar solicitud de contacto
    // =====================================================
    @PostMapping("/request")
    public ResponseEntity<ContactResponse> sendContactRequest(
            @RequestParam("username") String targetUsername,
            Authentication authentication
    ) {
        String requesterUsername = authentication.getName();

        return contactService.sendOrResendRequest(requesterUsername, targetUsername)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}