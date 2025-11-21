package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.Session;
import mx.edu.unpa.ChatEnRed.repositories.SessionRepository;
import mx.edu.unpa.ChatEnRed.services.SessionService;

@Service
public class SessionServiceImpl implements SessionService{

	@Autowired
	private SessionRepository sessionRepository;
	
	@Override
	@Transactional(readOnly=true)
	public Iterable<Session> findAll() {
		// TODO Auto-generated method stub
		return this.sessionRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Session> findById(Integer id) {
		// TODO Auto-generated method stub
		return this.sessionRepository.findById(id);
	}

	@Override
	@Transactional
	public Session save(Session session) {
		// TODO Auto-generated method stub
		return this.sessionRepository.save(session);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		this.sessionRepository.deleteById(id);
		
	}

}
