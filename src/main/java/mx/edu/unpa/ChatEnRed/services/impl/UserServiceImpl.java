package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Response.UserResponse;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.UserMapper;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<UserResponse> save(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        User user = this.userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return Optional.of(user)
				.map(userRepository::save)
				.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return Optional.of(true);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UserResponse> update(Integer id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        existing.setEmail(request.getEmail());
        existing.setUsername(request.getUsername());

        // Solo actualizamos estatus si viene en el request
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }

        return Optional.of(existing)
				.map(userRepository::save)
				.map(userMapper::toResponse);
    }
}