package mx.edu.unpa.ChatEnRed.controllers;

import java.util.LinkedList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.services.ConversationService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/conversation")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    @GetMapping(path = "/app")
    public Iterable<Conversation> index() {
        return this.conversationService.findAll();
    }
    
    @GetMapping("/add")
    public String add(Conversation conversation) {
        return "add";
    }
    
    @GetMapping("/fnd")
    public ResponseEntity<?> read(@RequestParam("id") Integer conversationId) {
        Optional<Conversation> oConversation = this.conversationService.findById(conversationId);
        if(oConversation.isPresent()) {
            LinkedList<Conversation> conversationList = new LinkedList<>();
            conversationList.add(oConversation.get());
            return ResponseEntity.ok(conversationList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation vacio");
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<Conversation> create(@RequestBody Conversation conversation) {
        Conversation saveConversation = this.conversationService.save(conversation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveConversation);
    }
    
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer conversationId) {
        Optional<Conversation> oConversation = this.conversationService.findById(conversationId);
        if(oConversation.isPresent()) {
            this.conversationService.deleteById(conversationId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/upd/{id}")
    public ResponseEntity<Conversation> upd(@PathVariable(value = "id") Integer conversationId) {
        Optional<Conversation> oConversation = this.conversationService.findById(conversationId);
        if(oConversation.isPresent()) {
            return ResponseEntity.ok(oConversation.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/save")
    public ResponseEntity<Conversation> save(@RequestBody Conversation conversation) {
        if(conversation != null) {
            this.conversationService.save(conversation);
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}