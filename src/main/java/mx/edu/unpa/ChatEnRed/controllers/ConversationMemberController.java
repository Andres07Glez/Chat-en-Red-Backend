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
import mx.edu.unpa.ChatEnRed.domains.ConversationMember;
import mx.edu.unpa.ChatEnRed.services.ConversationMemberService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/conversation-members")
public class ConversationMemberController {
	@Autowired
	private ConversationMemberService conversationMemberService;
	@GetMapping(path = "/app")
	public Iterable<ConversationMember> index(){
		return this.conversationMemberService.findAll();
		
	}
	
	@GetMapping("/add")
	public String add(ConversationMember conversationMember){
		return "add";	
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") Integer conversationMemberId) {
		Optional<ConversationMember> oConversationMember=this.conversationMemberService.findById(conversationMemberId);
		if(oConversationMember.isPresent()) {
			LinkedList<ConversationMember> conversationMemberList=new LinkedList<>();
			conversationMemberList.add(oConversationMember.get());
			return ResponseEntity.ok(conversationMemberList);
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation vacio");
			
		}
		
	}
	@PostMapping("/create")
	public ResponseEntity<ConversationMember> create(@RequestBody ConversationMember conversationMember) {
		
		ConversationMember saveConversationMember=this.conversationMemberService.save(conversationMember);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveConversationMember);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer conversationMemberId) {
		Optional<ConversationMember> oConversationMember=this.conversationMemberService.findById(conversationMemberId);
		if(oConversationMember.isPresent()) {
			this.conversationMemberService.deleteById(conversationMemberId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<ConversationMember> upd(@PathVariable(value="id") Integer conversationMemberId) {
		Optional<ConversationMember> oConversationMember=this.conversationMemberService.findById(conversationMemberId);
		if(oConversationMember.isPresent()) {
			return ResponseEntity.ok(oConversationMember.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<ConversationMember> save(@RequestBody ConversationMember conversationMember) {
		if(conversationMember != null) {
			this.conversationMemberService.save(conversationMember);
			return ResponseEntity.status(HttpStatus.CREATED).body(conversationMember);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}
	

}
