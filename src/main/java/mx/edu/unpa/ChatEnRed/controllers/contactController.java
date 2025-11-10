package mx.edu.unpa.ChatEnRed.controllers;

import java.util.LinkedList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.unpa.ChatEnRed.domains.Contact;
import mx.edu.unpa.ChatEnRed.services.ContactService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/contact")
public class contactController {
	@Autowired
	private ContactService contactService;
	@GetMapping(path ="/app")
	public Iterable<Contact> index(){
		return this.contactService.findAll();
	}
	
	@GetMapping("/add")
	public String add(Contact contact) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") int contactId){
		Optional<Contact> oContact=this.contactService.FindById(contactId);
		if(oContact.isPresent()) {
			LinkedList<Contact> contactList=new LinkedList<>();
			contactList.add(oContact.get());
			return ResponseEntity.ok(contactList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("contact vacio");
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<Contact> create(@RequestBody Contact contact){
		
		Contact saveContact=this.contactService.save(contact);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveContact);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") int contactId){
		Optional<Contact> oContact=this.contactService.FindById(contactId);
		if(oContact.isPresent()) {
			this.contactService.deleteById(contactId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<Contact> upd(@PathVariable(value="id") int contactId){
		Optional<Contact> oContact=this.contactService.FindById(contactId);
		if(oContact.isPresent()) {
			return ResponseEntity.ok(oContact.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			
		}
	}
	@PutMapping("/save")
	public ResponseEntity<Contact> save(@RequestBody Contact contact){
		if(contact != null) {
			this.contactService.save(contact);
			return ResponseEntity.status(HttpStatus.CREATED).body(contact);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();		}
		
	}

}
