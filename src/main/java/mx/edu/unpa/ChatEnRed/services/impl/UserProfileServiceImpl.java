package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request.UserProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response.UserProfileResponse;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.mappers.UserProfileMapper;
import mx.edu.unpa.ChatEnRed.repositories.UserProfileRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> findAll() {
        return userProfileRepository.findAll().stream()
                .map(userProfileMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileResponse> findById(Integer userId) {
        return userProfileRepository.findById(userId)
                .map(userProfileMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<UserProfileResponse> save(UserProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        UserProfile profile = userProfileMapper.toEntity(request, user);

        return Optional.of(profile)
				.map(userProfileRepository::save)
				.map(userProfileMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer userId) {
        return userProfileRepository.findById(userId)
                .map(entity -> {
                    userProfileRepository.deleteById(userId);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<UserProfileResponse> update(Integer userId, UserProfileRequest request) {
        UserProfile existing = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserProfile not found for userId: " + userId));

        return Optional.of(existing)
				.map(userProfileRepository::save)
				.map(userProfileMapper::toResponse);
    }
}