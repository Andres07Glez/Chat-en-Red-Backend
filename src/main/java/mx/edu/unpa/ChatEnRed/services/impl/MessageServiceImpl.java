package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.repositories.MessageRepository;
import mx.edu.unpa.ChatEnRed.services.MessageService;

@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	private MessageRepository messageRepository;

	@Override
	@Transactional(readOnly=true)
	public Iterable<Message> findAll() {
		// TODO Auto-generated method stub
		return this.messageRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Message> findById(Integer id) {
		// TODO Auto-generated method stub
		return this.messageRepository.findById(id);
	}

	@Override
	@Transactional
	public Message save(Message message) {
		// TODO Auto-generated method stub
		return this.messageRepository.save(message);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		this.messageRepository.deleteById(id);
		
	}

}
