package mx.edu.unpa.ChatEnRed.controllers;

import mx.edu.unpa.ChatEnRed.DTOs.Auth.JwtResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Auth.LoginRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Auth.SignupRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserRequest;
import mx.edu.unpa.ChatEnRed.security.jwt.JwtUtils;
import mx.edu.unpa.ChatEnRed.security.services.UserDetailsImpl;
import mx.edu.unpa.ChatEnRed.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Token generado para usuario {}: {}", loginRequest.getUsername(), jwt);

        // 5. Retornar respuesta
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        // 1. Convertir el SignupRequest (público) a UserRequest (interno)
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(signUpRequest.getUsername());
        userRequest.setEmail(signUpRequest.getEmail());
        userRequest.setPassword(signUpRequest.getPassword());
        userRequest.setDisplayName(signUpRequest.getDisplayName());
        userRequest.setIsActive(true);

        // 3. Guardar usando tu servicio (que ya se encarga de encriptar la contraseña)
        return userService.save(userRequest)
                .map(user -> ResponseEntity.ok(Collections.singletonMap("message", "Usuario registrado exitosamente")))
                .orElseGet(() -> ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error: El usuario o email ya existe")));
    }
}
