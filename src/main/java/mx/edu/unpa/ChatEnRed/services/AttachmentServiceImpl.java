package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.Attachment;
import mx.edu.unpa.ChatEnRed.repositories.AttachmentRepository;

@Service
public class AttachmentServiceImpl implements AttachmentService {

	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Override
	@Transactional(readOnly=true)
	public Iterable<Attachment> findAll() {
		// TODO Auto-generated method stub
		return this.attachmentRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Attachment> findById(int id) {
		// TODO Auto-generated method stub
		return this.attachmentRepository.findById(id);
	}

	@Override
	@Transactional
	public Attachment save(Attachment attachment) {
		// TODO Auto-generated method stub
		return this.attachmentRepository.save(attachment);
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		// TODO Auto-generated method stub
		this.attachmentRepository.deleteById(id);
		
	}
	

}
