package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="sessions")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private int id;
	//llave foranea
	@Column(name="user_id")
	private int userId;
	@Column(name="refresh_token_hash")
	private String refreshToken;
	@Column(name="device_info")
	private String deviceInfo;
	@Column(name="created_at")
	private LocalDate createdAt;
	@Column(name="expires_at")
	private LocalDate expiresAt;
	
}
