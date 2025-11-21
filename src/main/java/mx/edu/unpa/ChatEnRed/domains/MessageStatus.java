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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "message_status",
    uniqueConstraints = @UniqueConstraint(name = "ux_msg_recipient", columnNames = {"message_id","recipient_id"}),
    indexes = {
        @Index(name = "idx_msgstatus_recipient", columnList = "recipient_id, delivered, reading")
    }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // constructor requerido por JPA
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MessageStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    @ToString.Include
    private Boolean delivered = Boolean.FALSE;

    @Column(name = "delivered_at")
    @ToString.Include
    private LocalDateTime deliveredAt;

    /**
     * Campo 'reading' indica leído (true/false).
     */
    @Column(name = "reading", nullable = false)
    @ToString.Include
    private Boolean reading = Boolean.FALSE;

    @Column(name = "read_at")
    @ToString.Include
    private LocalDateTime readAt;

    /**
     * Automáticamente establece timestamps cuando las banderas se activan.
     * Nota: esto no detecta transiciones complejas en memoria — si gestionas
     * cambios desde el servicio, puedes establecer explícitamente las fechas ahí.
     */
    @PrePersist
    protected void prePersistTimestamps() {
        if (Boolean.TRUE.equals(delivered) && deliveredAt == null) {
            deliveredAt = LocalDateTime.now();
        }
        if (Boolean.TRUE.equals(reading) && readAt == null) {
            readAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdateTimestamps() {
        // Si la bandera se activa y aún no tiene fecha, la fijamos.
        if (Boolean.TRUE.equals(delivered) && deliveredAt == null) {
            deliveredAt = LocalDateTime.now();
        }
        if (Boolean.TRUE.equals(reading) && readAt == null) {
            readAt = LocalDateTime.now();
        }
    }
}