package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);


    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
