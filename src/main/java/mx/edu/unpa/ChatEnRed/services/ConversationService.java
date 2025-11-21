package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Conversation;

public interface ConversationService {
    public Iterable<Conversation> findAll();
    public Optional<Conversation> findById(Integer id);
    public Conversation save(Conversation conversation);
    public void deleteById(Integer id);
}