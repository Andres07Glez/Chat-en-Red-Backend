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

import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.services.MessageService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/message")
public class MessageController {
	@Autowired
	private MessageService messageService;
	@GetMapping(path = "/app")
	public Iterable<Message> index() {
		return this.messageService.findAll();
		
	}
	@GetMapping("/add")
	public String add(Message message) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") Integer messageId) {
		Optional<Message> oMessage=this.messageService.findById(messageId);
		if(oMessage.isPresent()) {
			LinkedList<Message> messageList=new LinkedList<>();
			messageList.add(oMessage.get());
			return ResponseEntity.ok(messageList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message vacio");
			
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<Message> create(@RequestBody Message message) {
		
		Message saveMessage=this.messageService.save(message);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveMessage);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer messageId) {
		Optional<Message> oMessage=this.messageService.findById(messageId);
		if(oMessage.isPresent()) {
			this.messageService.deleteById(messageId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<Message> upd(@PathVariable(value="id") Integer messageId) {
		Optional<Message> oMessage=this.messageService.findById(messageId);
		if(oMessage.isPresent()) {
			return ResponseEntity.ok(oMessage.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<Message> save(@RequestBody Message message) {
		if(message != null) {
			this.messageService.save(message);
			return ResponseEntity.status(HttpStatus.CREATED).body(message);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}

}
