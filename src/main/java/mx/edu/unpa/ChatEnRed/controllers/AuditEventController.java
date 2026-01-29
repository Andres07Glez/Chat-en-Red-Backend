package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Request.AuditEventRequest;
import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Response.AuditEventResponse;
import mx.edu.unpa.ChatEnRed.services.AuditEventService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/audit-event")
public class AuditEventController {

    @Autowired
    private AuditEventService auditEventService;

    @GetMapping(path = "/app")
	ResponseEntity<List<AuditEventResponse>> findAll() {
		return Optional
                .of(this.auditEventService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}

    @GetMapping("/fnd")
    public ResponseEntity<AuditEventResponse> findById(@RequestParam("id") Integer id) {
        return auditEventService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<AuditEventResponse> save(@RequestBody AuditEventRequest request) {
        return auditEventService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        return auditEventService.deleteById(id)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AuditEventResponse> update(
            @PathVariable("id") Integer id,
            @RequestBody AuditEventRequest request) {
        return auditEventService.update(id, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}