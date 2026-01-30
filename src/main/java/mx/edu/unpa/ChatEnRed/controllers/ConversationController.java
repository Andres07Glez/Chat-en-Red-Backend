package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.services.ConversationService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping(path = "/app")
	ResponseEntity<List<ConversationResponse>> findAll() {
		return Optional
                .of(this.conversationService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}

    @GetMapping("/fnd")
    public ResponseEntity<ConversationResponse> findById(@RequestParam("id") Integer conversationId) {
        return conversationService.findById(conversationId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<ConversationResponse> save(@RequestBody ConversationRequest request) {
        return conversationService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer conversationId) {
        return conversationService.deleteById(conversationId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ConversationResponse> update(
            @PathVariable("id") Integer conversationId,
            @RequestBody ConversationRequest request) {
        return conversationService.update(conversationId, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
    @GetMapping(path = "/list/{userId}")
    public ResponseEntity<List<ChatListItemDTO>> getChatList(@PathVariable Integer userId) {
        // Usamos la misma estructura funcional que tu MessageController
        return Optional.ofNullable(this.conversationService.getChatList(userId))
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());

    }
}