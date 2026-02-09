package mx.edu.unpa.ChatEnRed.domains;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "conversation_keys")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con la Conversación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    // Relación con el Usuario (Dueño de esta copia de la llave)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // La llave del grupo (AES-256) cifrada con la pública del usuario
    @Column(name = "encrypted_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedKey;

    // El IV usado para cifrar la llave (no el mensaje, sino la llave misma)
    @Column(name = "iv", nullable = false, length = 50)
    private String iv;
}