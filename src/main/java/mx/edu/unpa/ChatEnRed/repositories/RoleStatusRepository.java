package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;
import mx.edu.unpa.ChatEnRed.domains.RoleStatus;
public interface RoleStatusRepository extends JpaRepository<RoleStatus, Integer>{

}
