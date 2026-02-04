package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.services.MessageService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/messages")
public class MessageController {
	@Autowired
	private MessageService messageService;
	
	@GetMapping(path = "/app")
	ResponseEntity<List<MessageResponse>> findAll() {
		return Optional
                .of(this.messageService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}
	
	
	@GetMapping("/fnd")
	public ResponseEntity<MessageResponse> findById(@RequestParam("id") Integer messageId) {
		return this.messageService.findById(messageId)
				.map(ResponseEntity::ok)
				.orElseGet(ResponseEntity.notFound()::build);
	}
	
	@PostMapping("/create")
	public ResponseEntity<MessageResponse> save( @RequestBody MessageRequest request) {
		
		return this.messageService.save(request)
				.map(mss->ResponseEntity.ok().body(mss))
				.orElseGet(()->ResponseEntity.badRequest().build());
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Integer messageId) {
		return this.messageService.deleteById(messageId)
				.map(deleted -> ResponseEntity.noContent().build())
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	/*@GetMapping("/upd/{id}")
	public ResponseEntity<Message> upd(@PathVariable(value="id") Integer messageId) {
		Optional<Message> oMessage=this.messageService.findById(messageId);
		if(oMessage.isPresent()) {
			return ResponseEntity.ok(oMessage.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}*/
	
	@PutMapping("/update/{id}")
	public ResponseEntity<MessageResponse> update(
			@PathVariable(value="id") Integer messageId,
			@RequestBody MessageRequest request) {
		
		return this.messageService.update(messageId,request)
				.map(mss->ResponseEntity.ok().body(mss))
				.orElseGet(()->ResponseEntity.badRequest().build());
		
		
	}
	@GetMapping("/{conversationId}")
	public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Integer conversationId) {
		// Obtenemos el usuario del Token
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<MessageResponse> messages = messageService.getChatMessages(conversationId, username);
		return ResponseEntity.ok(messages);
	}

}
