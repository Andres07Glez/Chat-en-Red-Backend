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
}
