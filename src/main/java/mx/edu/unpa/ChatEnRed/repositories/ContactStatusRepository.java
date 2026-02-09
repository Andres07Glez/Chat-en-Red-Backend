package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.domains.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContactStatusRepository extends JpaRepository<ContactStatus, Integer> {
    // Añade esto para que el Service pueda buscar por "PENDING" o "ACCEPTED"
    Optional<ContactStatus> findByCode(String code);
}