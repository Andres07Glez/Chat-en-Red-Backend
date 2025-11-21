package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.repositories.ContactRepository;
import mx.edu.unpa.ChatEnRed.services.ContactService;

@Service
public class ContactServicelmpl implements ContactService {
	@Autowired
	private ContactRepository contactRepository;
	
	@Override
	@Transactional(readOnly=true)
	public Iterable<Contact> findAll(){
		return this.contactRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Contact> FindById(Integer id) {
		// TODO Auto-generated method stub
		return this.contactRepository.findById(id);
	}

	@Override
	@Transactional
	public Contact save(Contact contact) {
		// TODO Auto-generated method stub
		return this.contactRepository.save(contact);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		this.contactRepository.deleteById(id);
		
	}

}
