package mx.edu.unpa.ChatEnRed.controllers;

import mx.edu.unpa.ChatEnRed.DTOs.Auth.JwtResponse;
import mx.edu.unpa.ChatEnRed.DTOs.Auth.LoginRequest;
import mx.edu.unpa.ChatEnRed.security.jwt.JwtUtils;
import mx.edu.unpa.ChatEnRed.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Autenticar con usuario y contraseña
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Establecer contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Obtener detalles del usuario
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Token generado para usuario {}: {}", loginRequest.getUsername(), jwt);

        // 5. Retornar respuesta
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }
}
