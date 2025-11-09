package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Attachment;


public interface AttachmentService {

	public Iterable<Attachment> findAll();
	//public Page<Teacher> findAll(Pageable pageable);
	public Optional<Attachment> findById(int id);
	public Attachment save(Attachment attachment);
	public void deleteById(int id);
}
