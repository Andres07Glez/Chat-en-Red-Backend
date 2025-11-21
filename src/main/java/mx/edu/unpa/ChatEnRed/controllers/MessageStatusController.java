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

import mx.edu.unpa.ChatEnRed.domains.MessageStatus;
import mx.edu.unpa.ChatEnRed.services.MessageStatusService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/message-status")
public class MessageStatusController {
    
    @Autowired
    private MessageStatusService messageStatusService;
    
    @GetMapping(path = "/app")
    public Iterable<MessageStatus> index() {
        return this.messageStatusService.findAll();
    }
    
    @GetMapping("/add")
    public String add(MessageStatus messageStatus) {
        return "add";
    }
    
    @GetMapping("/fnd")
    public ResponseEntity<?> read(@RequestParam("id") Integer messageStatusId) {
        Optional<MessageStatus> oMessageStatus = this.messageStatusService.findById(messageStatusId);
        if(oMessageStatus.isPresent()) {
            LinkedList<MessageStatus> messageStatusList = new LinkedList<>();
            messageStatusList.add(oMessageStatus.get());
            return ResponseEntity.ok(messageStatusList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MessageStatus vacio");
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<MessageStatus> create(@RequestBody MessageStatus messageStatus) {
        MessageStatus saveMessageStatus = this.messageStatusService.save(messageStatus);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveMessageStatus);
    }
    
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer messageStatusId) {
        Optional<MessageStatus> oMessageStatus = this.messageStatusService.findById(messageStatusId);
        if(oMessageStatus.isPresent()) {
            this.messageStatusService.deleteById(messageStatusId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/upd/{id}")
    public ResponseEntity<MessageStatus> upd(@PathVariable(value = "id") Integer messageStatusId) {
        Optional<MessageStatus> oMessageStatus = this.messageStatusService.findById(messageStatusId);
        if(oMessageStatus.isPresent()) {
            return ResponseEntity.ok(oMessageStatus.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/save")
    public ResponseEntity<MessageStatus> save(@RequestBody MessageStatus messageStatus) {
        if(messageStatus != null) {
            this.messageStatusService.save(messageStatus);
            return ResponseEntity.status(HttpStatus.CREATED).body(messageStatus);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}