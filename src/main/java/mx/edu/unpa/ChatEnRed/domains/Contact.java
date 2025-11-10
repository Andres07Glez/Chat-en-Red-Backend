package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import mx.edu.unpa.ChatEnRed.enums.ContactStatus;

@Data
@Entity
@Table(name="contacts")
public class Contact implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
	private int id;
	@Column(name="owner_id")
	private int ownerId;
	@Column(name="contact_user_id")
	private int contactUserId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private ContactStatus status = ContactStatus.PENDING;
	
	
	@Column(name="created_at")
	private LocalDate createdAt;
	@Column(name="updated_at")
	private LocalDate updatedAt;
	
	
	
	
	
	

}
