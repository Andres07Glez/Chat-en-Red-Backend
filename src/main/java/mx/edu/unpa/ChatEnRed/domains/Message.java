package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_messages_sender", columnList = "sender_id"),
    @Index(name = "idx_conv_created", columnList = "conversation_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA necesita constructor sin-args; protegido evita uso accidental
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * Puede ser null para mensajes del sistema (SYSTEM), por eso lo dejamos opcional.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_type_id", nullable = false)
    private MessageType messageType;

    /**
     * Contiene el ciphertext en Base64.
     * Usamos 'TEXT' de MySQL (64KB). Si esperas mensajes encriptados MUY largos,
     * cambia columnDefinition a "LONGTEXT".
     */
    @Lob
    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    // Es crítico guardarlo para poder descifrar el mensaje después.
    @Column(length = 50)
    private String iv;

    @Column(name = "created_at", nullable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    @ToString.Include
    private LocalDateTime editedAt;

    //  Para borrado lógico (Soft Delete)
    // Si este campo tiene valor, el mensaje se considera eliminado.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Si haces un "soft delete" (actualizas deletedAt), JPA disparará este @PreUpdate
        // y cambiará también el editedAt.
        // Si quieres evitar eso, tendrás que manejar el 'editedAt' manualmente en tu Servicio
        // o usar una lógica condicional aquí (aunque @PreUpdate no sabe qué campo cambió).
        if (deletedAt == null) {
            editedAt = LocalDateTime.now();
        }
    }
	
	
}
