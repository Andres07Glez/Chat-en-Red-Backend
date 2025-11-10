package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.ConversationMember;
import mx.edu.unpa.ChatEnRed.repositories.ConversationMemberRepository;
import mx.edu.unpa.ChatEnRed.services.ConversationMemberService;

@Service
public class ConversationMemberServicioImpl implements ConversationMemberService {
	
	@Autowired
	private ConversationMemberRepository conversationMemberRepository;
	
	@Override
	@Transactional(readOnly=true)
	public Iterable<ConversationMember> findAll() {
		// TODO Auto-generated method stub
		return this.conversationMemberRepository.findAll() ;
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<ConversationMember> findById(int id) {
		// TODO Auto-generated method stub
		return this.conversationMemberRepository.findById(null);
	}

	@Override
	@Transactional
	public ConversationMember save(ConversationMember conversationMember) {
		// TODO Auto-generated method stub
		return this.conversationMemberRepository.save(conversationMember) ;
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		// TODO Auto-generated method stub
		this.conversationMemberRepository.deleteById(id);
		
	}
	
	

}
