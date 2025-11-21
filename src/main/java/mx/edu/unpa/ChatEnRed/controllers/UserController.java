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

import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.services.UserService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@GetMapping(path = "/app")
	public Iterable<User> index() {
		return this.userService.findAll();
		
	}
	@GetMapping("/add")
	public String add(User user) {
		return "add";
	}
	
	@GetMapping("/fnd")
	public ResponseEntity<?> read(@RequestParam("id") Integer userEventId) {
		Optional<User> oUser=this.userService.findById(userEventId);
		if(oUser.isPresent()) {
			LinkedList<User> userList=new LinkedList<>();
			userList.add(oUser.get());
			return ResponseEntity.ok(userList);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User vacio");
			
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> create(@RequestBody User user) {
		
		User saveUser=this.userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
	}
	
	@DeleteMapping("/del/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer userId) {
		Optional<User> oUser=this.userService.findById(userId);
		if(oUser.isPresent()) {
			this.userService.deleteById(userId);
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping("/upd/{id}")
	public ResponseEntity<User> upd(@PathVariable(value="id") Integer userId) {
		Optional<User> oUser=this.userService.findById(userId);
		if(oUser.isPresent()) {
			return ResponseEntity.ok(oUser.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/save")
	public ResponseEntity<User> save(@RequestBody User user) {
		if(user != null) {
			this.userService.save(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		
	}

}
