package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.MessageType;

import java.util.Optional;

@Repository
public interface MessageTypeRepository extends JpaRepository<MessageType,Integer>{
	Optional<MessageType> findByCode(String code);

}
