package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);


    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    // En UserRepository.java - AÑADIR:
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "u.id != :excludeId AND u.isActive = true")
    List<User> searchByUsernameOrEmail(@Param("query") String query,
                                       @Param("excludeId") Integer excludeId);
}

