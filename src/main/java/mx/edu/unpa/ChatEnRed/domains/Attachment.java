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
@Table(name="attachments")
public class Attachment implements Serializable{
	private static final long serialVersionUID =1L;
	@Id
	private int id;
	//llave foranea
	@Column(name="message_id")
	private int messageId;
	private String filename;
	@Column(name="mime_type")
	private String mimeType;
	private int size;
	@Column(name="storage_url")
	private String storageURL;
	private String checksum;
	@Column(name="created_at")
	private LocalDate createdAt;
	
	
	

}
