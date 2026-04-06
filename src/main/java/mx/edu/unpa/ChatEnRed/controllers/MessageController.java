package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

	@PostMapping
	public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(messageService.sendMessage(request, username));
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Integer messageId) {
		return this.messageService.deleteById(messageId)
				.map(deleted -> ResponseEntity.noContent().build())
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@GetMapping("/{conversationId}")
	public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Integer conversationId) {
		// Obtenemos el usuario del Token
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<MessageResponse> messages = messageService.getChatMessages(conversationId, username);
		return ResponseEntity.ok(messages);
	}
	@DeleteMapping("/batch")
	public ResponseEntity<Void> deleteMessages(
			@RequestBody Map<String, List<Integer>> body,
			Authentication auth) {

		List<Integer> ids = body.get("messageIds");
		if (ids == null || ids.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		messageService.deleteMessages(ids, auth.getName());
		return ResponseEntity.noContent().build();
	}

}
