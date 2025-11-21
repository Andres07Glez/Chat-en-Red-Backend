package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.MessageStatus;
import mx.edu.unpa.ChatEnRed.repositories.MessageStatusRepository;
import mx.edu.unpa.ChatEnRed.services.MessageStatusService;

@Service
public class MessageStatusServiceImpl implements MessageStatusService {

    @Autowired
    private MessageStatusRepository messageStatusRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Iterable<MessageStatus> findAll() {
        return this.messageStatusRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessageStatus> findById(Integer id) {
        return this.messageStatusRepository.findById(id);
    }

    @Override
    @Transactional
    public MessageStatus save(MessageStatus messageStatus) {
        return this.messageStatusRepository.save(messageStatus);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        this.messageStatusRepository.deleteById(id);
    }
}