package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.domains.Message;
import org.springframework.transaction.annotation.Transactional;


public interface MessageService {
	List<MessageResponse> findAll();
	Optional<MessageResponse> findById(Integer id);
	Optional<Boolean> deleteById(Integer id);
	List<MessageResponse> getChatMessages(Integer conversationId, String currentUsername);
	MessageResponse sendMessage(MessageRequest request, String username);

	int deleteMessages(List<Integer> messageIds, String username);

}
