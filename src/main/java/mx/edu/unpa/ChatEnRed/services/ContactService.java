package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Contact;

public interface ContactService {
	public Iterable<Contact> findAll();
	public Optional<Contact> FindById(Integer id);
	public Contact save(Contact contact);
	public void deleteById(Integer id);
	
	
	

}
