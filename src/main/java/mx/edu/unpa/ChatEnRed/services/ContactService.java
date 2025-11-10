package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Contact;

public interface ContactService {
	public Iterable<Contact> findAll();
	public Optional<Contact> FindById(int id);
	public Contact save(Contact contact);
	public void deleteById(int id);
	
	
	

}
