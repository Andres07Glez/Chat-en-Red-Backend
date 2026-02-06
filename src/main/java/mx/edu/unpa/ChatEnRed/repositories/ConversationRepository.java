package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.Conversation;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMember cm ON c.id = cm.conversation.id " +
            "WHERE cm.user.id = :userId " +
            "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") Integer userId);

}