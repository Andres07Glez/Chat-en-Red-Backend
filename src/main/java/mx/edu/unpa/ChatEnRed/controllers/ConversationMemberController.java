package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Request.ConversationMemberRequest;
import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Response.ConversationMemberResponse;
import mx.edu.unpa.ChatEnRed.services.ConversationMemberService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/conversation-member")
public class ConversationMemberController {

    @Autowired
    private ConversationMemberService conversationMemberService;

    @GetMapping (path="/app")
    ResponseEntity<List<ConversationMemberResponse>> findAll() {
        return Optional
        		.of(this.conversationMemberService.findAll())
        		.map(ResponseEntity::ok)
        		.orElseGet(ResponseEntity.notFound()::build);
        		
    }

    @GetMapping("/fnd")
    public ResponseEntity<ConversationMemberResponse> findById(@RequestParam("id") Integer id) {
        return conversationMemberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<ConversationMemberResponse> save(@RequestBody ConversationMemberRequest request) {
        return conversationMemberService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        return conversationMemberService.deleteById(id)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ConversationMemberResponse> update(
            @PathVariable("id") Integer id,
            @RequestBody ConversationMemberRequest request) {
        return conversationMemberService.update(id, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}