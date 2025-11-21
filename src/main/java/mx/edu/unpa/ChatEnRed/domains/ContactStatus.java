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
@Table(name = "contact_statuses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA necesita constructor sin-args; protegido evita uso accidental
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContactStatus implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Integer id;

	/**
	 * Código identificador (ej: "PENDING", "ACCEPTED").
	 * unique = true para evitar duplicados en la tabla.
	*/  
	@Column(nullable = false, length = 20, unique = true)
	@ToString.Include
	private String code;

	/**
	 * Etiqueta legible para mostrar en UI (puede ser nula si prefieres).
	*/
	@Column(length = 50)
	@ToString.Include
	private String label;


}
