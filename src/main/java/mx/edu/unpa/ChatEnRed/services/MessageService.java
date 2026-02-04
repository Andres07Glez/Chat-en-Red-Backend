package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.domains.Message;
import org.springframework.transaction.annotation.Transactional;


public interface MessageService {
	public List<MessageResponse> findAll();
	//public Page<Teacher> findAll(Pageable pageable);
	public Optional<MessageResponse> findById(Integer id);
	public Optional<MessageResponse> save(MessageRequest request);
	public Optional<Boolean> deleteById(Integer id);
	public Optional<MessageResponse> update(Integer id,MessageRequest request);
	List<MessageResponse> getChatMessages(Integer conversationId, String currentUsername);
}
