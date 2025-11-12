package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name="users")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "email", length = 300)
    private String email;
    
    @Column(name = "password_hash")
    private String password_hash;
    
    @Column(name = "is_active")
    private int is_active;
    
    @Column(name = "created_at")
    private LocalDateTime created_at; 

    @Column(name = "last_seen ")
    private LocalDateTime last_seen ;  
}
