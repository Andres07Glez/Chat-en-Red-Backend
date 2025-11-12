package mx.edu.unpa.ChatEnRed.domains;

import java.io.Serializable;
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
@Table(name = "message_status")
public class MessageStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "message_id")
    private int messageId;
    
    @Column(name = "recipient_id")
    private int recipientId;
    
    @Column(name ="delivered")
    private Integer delivered;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name ="reading")
    private Integer reading;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
}