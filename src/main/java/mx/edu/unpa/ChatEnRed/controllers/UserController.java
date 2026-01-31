package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Session.Response.SessionResponse;
import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Response.UserResponse;
import mx.edu.unpa.ChatEnRed.services.UserService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

	@GetMapping
	ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
		
	}

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestBody UserRequest request) {
        return userService.save(request)
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer userId) {
        return userService.deleteById(userId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Integer userId,
            @RequestBody UserRequest request) {
        return userService.update(userId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}