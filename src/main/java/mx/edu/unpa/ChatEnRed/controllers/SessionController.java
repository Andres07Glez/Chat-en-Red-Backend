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

import mx.edu.unpa.ChatEnRed.domains.Session;
import mx.edu.unpa.ChatEnRed.services.SessionService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/session")
public class SessionController {
	@Autowired
	private SessionService sessionService;
	@GetMapping(path = "/app")
	public Iterable<Session> index() {
		return this.sessionService.findAll();
		
	}
	@GetMapping("/add")
	public String add(Session session) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") Integer sessionId) {
		Optional<Session> oSession=this.sessionService.findById(sessionId);
		if(oSession.isPresent()) {
			LinkedList<Session> sessionList=new LinkedList<>();
			sessionList.add(oSession.get());
			return ResponseEntity.ok(sessionList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session vacio");
			
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<Session> create(@RequestBody Session session) {
		
		Session saveSession=this.sessionService.save(session);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveSession);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer sessionId) {
		Optional<Session> oSession=this.sessionService.findById(sessionId);
		if(oSession.isPresent()) {
			this.sessionService.deleteById(sessionId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<Session> upd(@PathVariable(value="id") Integer sessionId) {
		Optional<Session> oSession=this.sessionService.findById(sessionId);
		if(oSession.isPresent()) {
			return ResponseEntity.ok(oSession.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<Session> save(@RequestBody Session session) {
		if(session != null) {
			this.sessionService.save(session);
			return ResponseEntity.status(HttpStatus.CREATED).body(session);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}

}
