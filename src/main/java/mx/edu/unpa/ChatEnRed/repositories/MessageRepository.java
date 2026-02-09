package mx.edu.unpa.ChatEnRed.repositories;

import java.time.LocalDateTime;
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
    // Cuenta mensajes en un chat que sean más nuevos que una fecha específica
    long countByConversationIdAndCreatedAtAfter(Integer conversationId, LocalDateTime date);

    // 1. Contar mensajes NO LEÍDOS (Posteriores a fecha X y que NO envié yo)
    long countByConversationIdAndCreatedAtAfterAndSenderIdNot(Integer conversationId, LocalDateTime date, Integer myUserId);

    // 2. Contar TODO (Si nunca he entrado, cuenta todo lo que NO envié yo)
    long countByConversationIdAndSenderIdNot(Integer conversationId, Integer myUserId);
    // Sobrecarga para contar TODO si lastReadAt es null (nunca ha entrado)
    long countByConversationId(Integer conversationId);
}
