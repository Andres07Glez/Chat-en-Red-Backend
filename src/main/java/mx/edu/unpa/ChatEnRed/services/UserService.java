package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.User;

public interface UserService {
	
	public Iterable<User> findAll();
	//public Page<Teacher> findAll(Pageable pageable);
	public Optional<User> findById(Integer id);
	public User save(User user);
	public void deleteById(Integer id);

}
