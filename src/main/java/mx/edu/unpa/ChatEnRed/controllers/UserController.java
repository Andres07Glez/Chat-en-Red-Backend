package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UpdateProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserKeyRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Response.UserMeResponse;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.repositories.UserProfileRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

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
    // 1. SUBIR MI LLAVE PÚBLICA (Al iniciar sesión o registrarse)
    @PostMapping("/keys")
    public ResponseEntity<Void> updatePublicKey(@RequestBody UserKeyRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Lógica simple: buscar usuario y actualizar campo
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setPublicKey(request.getPublicKey());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
    // 2. OBTENER LLAVE PÚBLICA DE OTRO USUARIO (Para iniciar chat)
    @GetMapping("/{userId}/key")
    public ResponseEntity<UserKeyRequest> getUserPublicKey(@PathVariable Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        UserKeyRequest response = new UserKeyRequest();
        response.setPublicKey(user.getPublicKey());

        return ResponseEntity.ok(response);
    }
    //3. Perfil completo del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyProfile(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        UserMeResponse response = new UserMeResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());

        // Agregar datos del perfil si existen
        userProfileRepository.findById(user.getId()).ifPresent(profile -> {
            response.setDisplayName(profile.getDisplayName());
            response.setBio(profile.getBio());
            response.setAvatarUrl(profile.getAvatarUrl());
        });

        return ResponseEntity.ok(response);
    }
    // 4. Actualizar perfil propio
    @PatchMapping("/me/profile")
    public ResponseEntity<UserMeResponse> updateMyProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Buscar perfil existente o crearlo si no existe aún
        UserProfile profile = userProfileRepository.findById(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        // Solo actualizar los campos que vienen en el request (null-safe)
        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName().isBlank() ? null : request.getDisplayName().trim());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio().isBlank() ? null : request.getBio().trim());
        }

        userProfileRepository.save(profile);

        // Construir y devolver respuesta actualizada
        UserMeResponse response = new UserMeResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setDisplayName(profile.getDisplayName());
        response.setBio(profile.getBio());
        response.setAvatarUrl(profile.getAvatarUrl());

        return ResponseEntity.ok(response);
    }


}