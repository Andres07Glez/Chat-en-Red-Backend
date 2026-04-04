package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.domains.ConversationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationKeyRepository extends JpaRepository<ConversationKey, Integer> {

    // Buscar la copia de la llave que le pertenece a un usuario específico
    Optional<ConversationKey> findByConversationIdAndUserId(Integer conversationId, Integer userId);
    @Query("""
            SELECT ck FROM ConversationKey ck
            JOIN FETCH ck.conversation c
            JOIN FETCH c.createdBy creator
            WHERE ck.conversation.id = :conversationId
              AND ck.user.id          = :userId
            """)
    Optional<ConversationKey> findWithCreatorByConversationIdAndUserId(
            @Param("conversationId") Integer conversationId,
            @Param("userId")         Integer userId
    );
}
