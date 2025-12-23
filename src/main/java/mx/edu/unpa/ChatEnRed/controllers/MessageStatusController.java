package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Contact.Response.ContactResponse;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Request.MessageStatusRequest;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Response.MessageStatusResponse;
import mx.edu.unpa.ChatEnRed.services.MessageStatusService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/message-status")
public class MessageStatusController {

    @Autowired
    private MessageStatusService messageStatusService;

    @GetMapping(path= "/app")
	ResponseEntity<List<MessageStatusResponse>> findAll() {
		return Optional
        		.of(this.messageStatusService.findAll())
        		.map(ResponseEntity::ok)
        		.orElseGet(ResponseEntity.notFound()::build);
    }
        

    @GetMapping("/fnd")
    public ResponseEntity<MessageStatusResponse> findById(@RequestParam("id") Integer id) {
        return messageStatusService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<MessageStatusResponse> save(@RequestBody MessageStatusRequest request) {
        return messageStatusService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        return messageStatusService.deleteById(id)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MessageStatusResponse> update(
            @PathVariable("id") Integer id,
            @RequestBody MessageStatusRequest request) {
        return messageStatusService.update(id, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}