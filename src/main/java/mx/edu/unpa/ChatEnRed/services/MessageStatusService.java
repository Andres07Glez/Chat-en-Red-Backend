package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Request.MessageStatusRequest;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Response.MessageStatusResponse;

public interface MessageStatusService {
    List<MessageStatusResponse> findAll();
    Optional<MessageStatusResponse> findById(Integer id);
    Optional<MessageStatusResponse> save(MessageStatusRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<MessageStatusResponse> update(Integer id, MessageStatusRequest request);
}