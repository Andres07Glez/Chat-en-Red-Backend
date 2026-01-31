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
    @Query(value = """
        SELECT 
            c.id AS conversationId,
            c.title AS title,
            ct.code AS conversationType,
            
            -- [CORRECCIÓN] Usamos ANY_VALUE() para evitar error ONLY_FULL_GROUP_BY
            ANY_VALUE(m.content) AS lastMessageContent,
            ANY_VALUE(m.iv) AS lastMessageIv,
            c.last_message_at AS lastMessageTime,
            ANY_VALUE(u_sender.username) AS lastMessageSender,
            
            -- Subconsulta (no necesita cambios)
            (SELECT COUNT(*) 
             FROM message_status ms 
             JOIN messages msg ON ms.message_id = msg.id 
             WHERE msg.conversation_id = c.id 
               AND ms.recipient_id = :currentUserId 
               AND ms.reading = 0
            ) AS unreadCount,
            
            -- [CORRECCIÓN] ANY_VALUE() para datos del "otro" usuario
            ANY_VALUE(other_u.id) AS otherUserId,
            ANY_VALUE(other_u.username) AS otherUserName,
            ANY_VALUE(up.avatar_url) AS otherUserAvatar

        FROM conversations c
        JOIN conversation_members cm ON c.id = cm.conversation_id
        JOIN conversation_types ct ON c.conversation_type_id = ct.id
        
        -- Join mensaje
        LEFT JOIN messages m ON m.conversation_id = c.id 
             AND m.created_at = c.last_message_at
        LEFT JOIN users u_sender ON m.sender_id = u_sender.id
        
        -- Join otro usuario
        LEFT JOIN conversation_members cm_other 
             ON c.id = cm_other.conversation_id 
             AND cm_other.user_id != :currentUserId
        LEFT JOIN users other_u ON cm_other.user_id = other_u.id
        LEFT JOIN user_profiles up ON other_u.id = up.user_id

        WHERE cm.user_id = :currentUserId
          AND (c.last_message_at IS NOT NULL OR c.created_at IS NOT NULL)
          AND (ct.code = 'GROUP' OR (ct.code = 'DIRECT' AND cm_other.user_id IS NOT NULL))
        
        -- [CORRECCIÓN] Agregamos las columnas principales de 'c' y 'ct' al GROUP BY
        GROUP BY c.id, c.title, ct.code, c.last_message_at
        
        ORDER BY c.last_message_at DESC
        """, nativeQuery = true)
    List<ChatListItemDTO> findChatListByUserId(@Param("currentUserId") Integer userId);

}