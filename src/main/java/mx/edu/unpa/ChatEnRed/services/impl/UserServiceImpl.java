package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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
        User user = this.userMapper.toEntity(request);

        return Optional.of(user)
				.map(userRepository::save)
				.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<UserResponse> update(Integer id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        return Optional.of(existing)
				.map(userRepository::save)
				.map(userMapper::toResponse);
    }
}