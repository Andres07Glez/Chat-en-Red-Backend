package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "message_types")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // constructor requerido por JPA; protegido para evitar uso accidental
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MessageType implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    /**
     * Código identificador (ej: "TEXT", "FILE", "SYSTEM").
     * unique = true para evitar duplicados.
     */
    @Column(nullable = false, length = 10, unique = true)
    @ToString.Include
    private String code;

    /**
     * Etiqueta legible para UI (opcional).
     */
    @Column(length = 30)
    @ToString.Include
    private String label;

}
