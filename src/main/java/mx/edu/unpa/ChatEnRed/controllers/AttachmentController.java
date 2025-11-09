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

import mx.edu.unpa.ChatEnRed.domains.Attachment;
import mx.edu.unpa.ChatEnRed.services.AttachmentService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/attachment")
public class AttachmentController {
	@Autowired
	private AttachmentService attachmentService;
	@GetMapping(path = "/app")
	public Iterable<Attachment> index() {
		return this.attachmentService.findAll();
		
	}
	@GetMapping("/add")
	public String add(Attachment attachment) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") int attachmentEventId) {
		Optional<Attachment> oAttachment=this.attachmentService.findById(attachmentEventId);
		if(oAttachment.isPresent()) {
			LinkedList<Attachment> attachmentList=new LinkedList<>();
			attachmentList.add(oAttachment.get());
			return ResponseEntity.ok(attachmentList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attachment vacio");
			
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<Attachment> create(@RequestBody Attachment attachment) {
		
		Attachment saveAttachment=this.attachmentService.save(attachment);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveAttachment);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") int attachmentId) {
		Optional<Attachment> oAttachment=this.attachmentService.findById(attachmentId);
		if(oAttachment.isPresent()) {
			this.attachmentService.deleteById(attachmentId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<Attachment> upd(@PathVariable(value="id") int attachmentId) {
		Optional<Attachment> oAttachment=this.attachmentService.findById(attachmentId);
		if(oAttachment.isPresent()) {
			return ResponseEntity.ok(oAttachment.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<Attachment> save(@RequestBody Attachment attachment) {
		if(attachment != null) {
			this.attachmentService.save(attachment);
			return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}
}
