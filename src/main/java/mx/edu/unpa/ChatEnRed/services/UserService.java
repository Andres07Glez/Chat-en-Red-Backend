package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Response.UserResponse;

public interface UserService {
    List<UserResponse> findAll();
    Optional<UserResponse> findById(Integer id);
    Optional<UserResponse> save(UserRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<UserResponse> update(Integer id, UserRequest request);
}