package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.unpa.ChatEnRed.domains.AuditEvent;
import mx.edu.unpa.ChatEnRed.repositories.AuditEventRepository;
import mx.edu.unpa.ChatEnRed.services.AuditEventService;

@Service
public class AuditEventServiceImpl implements AuditEventService{

	@Autowired
	private AuditEventRepository auditEventRepository;

	@Override
	@Transactional(readOnly=true)
	public Iterable<AuditEvent> findAll() {
		// TODO Auto-generated method stub
		return this.auditEventRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<AuditEvent> findById(Integer id) {
		// TODO Auto-generated method stub
		return this.auditEventRepository.findById(id);
	}

	@Override
	@Transactional
	public AuditEvent save(AuditEvent auditEvent) {
		// TODO Auto-generated method stub
		return this.auditEventRepository.save(auditEvent);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		this.auditEventRepository.deleteById(id);
		
	}
}
