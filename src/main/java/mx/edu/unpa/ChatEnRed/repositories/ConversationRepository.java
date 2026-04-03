package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.Conversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMember cm ON c.id = cm.conversation.id " +
            "WHERE cm.user.id = :userId " +
            "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") Integer userId);
    @Query("SELECT DISTINCT c FROM Conversation c " +
            "JOIN ConversationMember cm1 ON cm1.conversation.id = c.id " +
            "JOIN ConversationMember cm2 ON cm2.conversation.id = c.id " +
            "WHERE cm1.user.id = :userId1 " +
            "AND cm2.user.id = :userId2 " +
            "AND c.conversationType.code = 'DIRECT'")
    Optional<Conversation> findDirectConversationBetweenUsers(
            @Param("userId1") Integer userId1,
            @Param("userId2") Integer userId2
    );

}