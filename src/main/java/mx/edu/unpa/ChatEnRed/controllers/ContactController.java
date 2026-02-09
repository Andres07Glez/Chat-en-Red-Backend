package mx.edu.unpa.ChatEnRed.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Request.ContactRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.services.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor // Inyección automática
public class ContactController {

    private final ContactService contactService;

    // --- TUS MÉTODOS PARA LA VISTA DE SOLICITUDES ---

    // Obtener solicitudes que yo envié
    @GetMapping("/requests/sent/{userId}")
    public ResponseEntity<List<ContactResponse>> getSentRequests(@PathVariable Integer userId) {
        return ResponseEntity.ok(contactService.getSentRequests(userId));
    }

    // Obtener solicitudes que me llegaron
    @GetMapping("/requests/received/{userId}")
    public ResponseEntity<List<ContactResponse>> getReceivedRequests(@PathVariable Integer userId) {
        return ResponseEntity.ok(contactService.getReceivedRequests(userId));
    }

    // Aceptar una solicitud (Cambiamos de Post a Patch porque es una actualización parcial)
    @PatchMapping("/requests/{contactId}/accept")
    public ResponseEntity<ContactResponse> acceptRequest(
            @PathVariable Integer contactId,
            @RequestParam Integer userId) {
        return ResponseEntity.ok(contactService.acceptRequest(contactId, userId));
    }

    // Rechazar o eliminar solicitud
    @DeleteMapping("/requests/{contactId}")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Integer contactId,
            @RequestParam Integer userId) {
        contactService.rejectOrDeleteRequest(contactId, userId);
        return ResponseEntity.noContent().build();
    }

    // --- MÉTODO PARA TU AMIGO (Enviar solicitud) ---

    @PostMapping("/send-request")
    public ResponseEntity<ContactResponse> sendContactRequest(@RequestBody ContactRequest request) {
        return contactService.save(request)
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp))
                .orElse(ResponseEntity.badRequest().build());
    }
}