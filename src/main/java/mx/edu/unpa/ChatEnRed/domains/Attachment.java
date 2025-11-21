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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "attachments", indexes = {
    @Index(name = "idx_attachments_message", columnList = "message_id")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // constructor requerido por JPA
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachment implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(length = 255, nullable = false)
    @ToString.Include
    private String filename;

    @Column(name = "mime_type", length = 100, nullable = false)
    @ToString.Include
    private String mimeType;

    /**
     * Tamaño en bytes. Uso Long para permitir nulos si fuera necesario,
     * pero aquí lo marcamos no nulo porque normalmente un attachment tiene tamaño.
     */
    @Column(nullable = false)
    @ToString.Include
    private Integer size;

    /**
     * URL o path donde está almacenado el archivo. Por seguridad lo excluimos de toString()
     * para que no se loguee accidentalmente.
     */
    @Column(name = "storage_url", length = 500, nullable = false)
    private String storageUrl;

    @Column(length = 100)
    private String checksum;

    @Column(name = "created_at", nullable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
	
	
	
	

}
