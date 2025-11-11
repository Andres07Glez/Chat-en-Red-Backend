package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Message;



public interface MessageService {
	public Iterable<Message> findAll();
	//public Page<Teacher> findAll(Pageable pageable);
	public Optional<Message> findById(int id);
	public Message save(Message message);
	public void deleteById(int id);

}
