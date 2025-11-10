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
import mx.edu.unpa.ChatEnRed.enums.RoleStatus;

@Data
@Entity
@Table(name="Conversation_Members")
public class ConversationMember implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
	private int id;
	@Column(name="conversatio_id")
	private int conversatioId;
	@Column(name="user_id")
	private int userId;
	@Enumerated(EnumType.STRING)
	@Column(name="role")
	private RoleStatus status = RoleStatus.MEMBER;
	@Column(name="joined_at")
	private LocalDate joinedAt;
	
}
