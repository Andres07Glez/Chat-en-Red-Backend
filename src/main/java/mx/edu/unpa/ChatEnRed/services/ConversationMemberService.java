package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;

public interface ConversationMemberService {
	public Iterable<ConversationMember> findAll();
	 
	public Optional<ConversationMember> findById(Integer id);
	public ConversationMember save(ConversationMember conversationMember);
	public void deleteById(Integer id);
	

}
