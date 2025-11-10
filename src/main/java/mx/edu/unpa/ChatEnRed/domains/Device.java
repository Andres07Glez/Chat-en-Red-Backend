package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "devices")
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "public_key", length = 300)
    private String publicKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
