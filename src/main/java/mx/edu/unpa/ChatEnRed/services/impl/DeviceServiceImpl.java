package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.edu.unpa.ChatEnRed.domains.Device;
import mx.edu.unpa.ChatEnRed.repositories.DeviceRepository;
import mx.edu.unpa.ChatEnRed.services.DeviceService;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository repo;

    @Override
    public Iterable<Device> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Device> findById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public List<Device> findByUserId(Integer userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Device save(Device device) {
        return repo.save(device);
    }

    @Override
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
