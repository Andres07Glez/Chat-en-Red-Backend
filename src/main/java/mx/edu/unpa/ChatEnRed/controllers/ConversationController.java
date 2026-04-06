package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.CreateGroupRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationKeyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.services.ConversationService;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ChatListItemDTO>> getMyChats() {
        // SEGURIDAD: Obtenemos el username directamente del Token JWT
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ChatListItemDTO> chats = conversationService.getMyChatList(username);
        return chats.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(chats);
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
    @PostMapping("/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(@PathVariable Integer conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        conversationService.markAsRead(conversationId, username);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/group")
    public ResponseEntity<ChatListItemDTO> createGroup(
            @RequestBody CreateGroupRequest request,
            Authentication auth) {
        ChatListItemDTO dto = conversationService.createGroup(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    @PostMapping("/direct")
    public ResponseEntity<ChatListItemDTO> startDirectConversation(
            @RequestParam("targetUserId") Integer targetUserId,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                conversationService.findOrCreateDirectConversation(auth.getName(), targetUserId)
        );
    }
    @GetMapping("/{conversationId}/my-key")
    public ResponseEntity<ConversationKeyResponse> getMyConversationKey(
            @PathVariable Integer conversationId,
            Authentication auth) {
        return ResponseEntity.ok(
                conversationService.getMyConversationKey(conversationId, auth.getName())
        );
    }
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> leaveOrDelete(
            @PathVariable Integer conversationId,
            Authentication auth) {
        conversationService.leaveOrDeleteConversation(conversationId, auth.getName());
        return ResponseEntity.noContent().build();
    }

}