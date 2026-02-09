package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Integer> {
    @Query("SELECT cm.user FROM ConversationMember cm " +
            "WHERE cm.conversation.id = :convId AND cm.user.id <> :myId")
    User findOtherParticipant(@Param("convId") Integer convId, @Param("myId") Integer myId);

    boolean existsByConversationIdAndUserId(Integer conversationId, Integer id);

    @Query("SELECT m.user.id FROM ConversationMember m WHERE m.conversation.id = :chatId")
    List<Integer> findUserIdsByConversationId(@Param("chatId") Integer conversationId);

    Optional<ConversationMember> findByConversationIdAndUserId(Integer id, Integer id1);
}
