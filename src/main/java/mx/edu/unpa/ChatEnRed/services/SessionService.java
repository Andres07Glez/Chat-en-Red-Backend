package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Session;


public interface SessionService {
	public Iterable<Session> findAll();
	//public Page<Teacher> findAll(Pageable pageable);
	public Optional<Session> findById(Integer id);
	public Session save(Session session);
	public void deleteById(Integer id);

}
