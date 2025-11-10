package mx.edu.unpa.ChatEnRed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Integer> {
	

}
