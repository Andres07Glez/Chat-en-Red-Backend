package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Session.Request.SessionRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Session.Response.SessionResponse;

public interface SessionService {
    List<SessionResponse> findAll();
    Optional<SessionResponse> findById(Integer id);
    Optional<SessionResponse> save(SessionRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<SessionResponse> update(Integer id, SessionRequest request);
}