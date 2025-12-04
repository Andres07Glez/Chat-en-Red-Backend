package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.edu.unpa.ChatEnRed.domains.Attachment;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

}