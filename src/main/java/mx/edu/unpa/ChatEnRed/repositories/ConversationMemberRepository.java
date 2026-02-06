package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Integer> {
    @Query("SELECT cm.user FROM ConversationMember cm " +
            "WHERE cm.conversation.id = :convId AND cm.user.id <> :myId")
    User findOtherParticipant(@Param("convId") Integer convId, @Param("myId") Integer myId);

    boolean existsByConversationIdAndUserId(Integer conversationId, Integer id);
}
