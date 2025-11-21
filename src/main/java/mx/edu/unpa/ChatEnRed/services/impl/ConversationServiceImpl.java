package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.repositories.ConversationRepository;
import mx.edu.unpa.ChatEnRed.services.ConversationService;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Iterable<Conversation> findAll() {
        return this.conversationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findById(Integer id) {
        return this.conversationRepository.findById(id);
    }

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        return this.conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        this.conversationRepository.deleteById(id);
    }
}