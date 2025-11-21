package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.MessageStatus;

public interface MessageStatusService {
    public Iterable<MessageStatus> findAll();
    public Optional<MessageStatus> findById(Integer id);
    public MessageStatus save(MessageStatus messageStatus);
    public void deleteById(Integer id);
}