package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // constructor requerido por JPA
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Compartimos la PK con User (one-to-one, shared primary key).
     * userId es la clave primaria y también la FK hacia users.id
     */
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "user_id")
    private Integer userId;

    /**
     * Relación one-to-one con usuario. Usamos @MapsId para compartir PK.
     * Excluida de toString() para evitar LazyInitializationException y logs ruidosos.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "display_name", length = 100)
    @ToString.Include
    private String displayName;

    /**
     * URL del avatar (object storage, CDN, etc.). La excluimos de toString()
     * para no dejar URLs o tokens en logs accidentales.
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(length = 200)
    @ToString.Include
    private String bio;

    @Column(name = "updated_at")
    @ToString.Include
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
