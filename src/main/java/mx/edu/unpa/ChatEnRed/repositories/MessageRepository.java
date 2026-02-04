package mx.edu.unpa.ChatEnRed.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.domains.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
    // Obtiene el mensaje más reciente de una conversación
    Message findFirstByConversationIdOrderByCreatedAtDesc(Integer conversationId);
}
