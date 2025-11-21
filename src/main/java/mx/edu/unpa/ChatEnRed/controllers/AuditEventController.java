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

import mx.edu.unpa.ChatEnRed.domains.AuditEvent;
import mx.edu.unpa.ChatEnRed.services.AuditEventService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/audit-event")
public class AuditEventController {
	@Autowired
	private AuditEventService auditEventService;
	@GetMapping(path = "/app")
	public Iterable<AuditEvent> index() {
		return this.auditEventService.findAll();
		
	}
	@GetMapping("/add")
	public String add(AuditEvent auditEvent) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") Integer auditEventId) {
		Optional<AuditEvent> oAuditEvent=this.auditEventService.findById(auditEventId);
		if(oAuditEvent.isPresent()) {
			LinkedList<AuditEvent> auditEventList=new LinkedList<>();
			auditEventList.add(oAuditEvent.get());
			return ResponseEntity.ok(auditEventList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AuditEvents vacio");
			
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<AuditEvent> create(@RequestBody AuditEvent auditEvent) {
		
		AuditEvent saveAuditEvent=this.auditEventService.save(auditEvent);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveAuditEvent);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer auditEventId) {
		Optional<AuditEvent> oAuditEvent=this.auditEventService.findById(auditEventId);
		if(oAuditEvent.isPresent()) {
			this.auditEventService.deleteById(auditEventId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<AuditEvent> upd(@PathVariable(value="id") Integer auditEventId) {
		Optional<AuditEvent> oAuditEvent=this.auditEventService.findById(auditEventId);
		if(oAuditEvent.isPresent()) {
			return ResponseEntity.ok(oAuditEvent.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<AuditEvent> save(@RequestBody AuditEvent auditEvent) {
		if(auditEvent != null) {
			this.auditEventService.save(auditEvent);
			return ResponseEntity.status(HttpStatus.CREATED).body(auditEvent);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}
	

}
