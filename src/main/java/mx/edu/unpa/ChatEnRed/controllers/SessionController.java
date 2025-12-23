package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Session.Request.SessionRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Session.Response.SessionResponse;
import mx.edu.unpa.ChatEnRed.services.SessionService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

	@GetMapping(path = "/app")
	ResponseEntity<List<SessionResponse>> findAll() {
		return Optional
                .of(this.sessionService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}

    @GetMapping("/fnd")
    public ResponseEntity<SessionResponse> findById(@RequestParam("id") Integer sessionId) {
        return sessionService.findById(sessionId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<SessionResponse> save(@RequestBody SessionRequest request) {
        return sessionService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer sessionId) {
        return sessionService.deleteById(sessionId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SessionResponse> update(
            @PathVariable("id") Integer sessionId,
            @RequestBody SessionRequest request) {
        return sessionService.update(sessionId, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}