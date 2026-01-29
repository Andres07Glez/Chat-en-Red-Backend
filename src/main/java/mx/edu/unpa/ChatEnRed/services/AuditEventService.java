package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Request.AuditEventRequest;
import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Response.AuditEventResponse;

public interface AuditEventService {
    List<AuditEventResponse> findAll();
    Optional<AuditEventResponse> findById(Integer id);
    Optional<AuditEventResponse> save(AuditEventRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<AuditEventResponse> update(Integer id, AuditEventRequest request);
}