package mx.edu.unpa.ChatEnRed.repositories;

import mx.edu.unpa.ChatEnRed.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import mx.edu.unpa.ChatEnRed.domains.Contact;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{
    //nuevo
    List<Contact> findByOwner(User owner);

    //nuevo
    Optional<Contact> findByOwnerAndContactUser(User owner, User contactUser);

    // Relación B → A (solicitud entrante)
    Optional<Contact> findByContactUserAndOwner(User contactUser, User owner);

    // 1. Para filtrar por dueño y estado (Contactos aceptados o solicitudes enviadas)
    // Borramos la duplicación que tenías y dejamos solo uno.
    List<Contact> findByOwnerIdAndContactStatusCode(Integer ownerId, String statusCode);

    // 2. Para filtrar por quien recibe y estado (Solicitudes recibidas)
    List<Contact> findByContactUserIdAndContactStatusCode(Integer contactUserId, String statusCode);

    // 3. Para verificar si ya existe una relación (Evita duplicados)
    boolean existsByOwnerIdAndContactUserId(Integer ownerId, Integer contactUserId);

    // 4. Para buscar una relación específica entre dos personas
    Optional<Contact> findByOwnerIdAndContactUserId(Integer ownerId, Integer contactUserId);

}
