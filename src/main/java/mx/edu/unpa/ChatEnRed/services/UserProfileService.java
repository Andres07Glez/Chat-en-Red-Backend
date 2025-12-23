package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request.UserProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response.UserProfileResponse;

public interface UserProfileService {
    List<UserProfileResponse> findAll();
    Optional<UserProfileResponse> findById(Integer userId);
    Optional<UserProfileResponse> save(UserProfileRequest request);
    Optional<Boolean> deleteById(Integer userId);
    Optional<UserProfileResponse> update(Integer userId, UserProfileRequest request);
}