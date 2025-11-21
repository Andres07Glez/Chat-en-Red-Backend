package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "users", indexes = {
    @Index(name = "idx_users_last_seen", columnList = "last_seen")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA necesita constructor sin-args; protegido evita uso accidental
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    @ToString.Include
    private String username;

    @Column(length = 150, unique = true)
    @ToString.Include
    private String email;

    /**
     * Guardar SOLO el hash de la contraseña. Nunca el password en claro.
     * Excluido de toString() por seguridad.
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    /**
     * Usamos Boolean en lugar de primitive para poder detectar null si fuera necesario,
     * pero aseguramos valor por defecto en persist.
     */
    @Column(name = "is_active", nullable = false)
    @ToString.Include
    private Boolean isActive = Boolean.TRUE;

    /**
     * Fecha de creación (no actualizable).
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    /**
     * Última vez que el usuario estuvo activo/online. Se actualiza desde el servicio.
     */
    @Column(name = "last_seen")
    @ToString.Include
    private LocalDateTime lastSeen;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = Boolean.TRUE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // No tocar createdAt; lastSeen se actualiza desde el servicio cuando corresponde.
    } 
}
