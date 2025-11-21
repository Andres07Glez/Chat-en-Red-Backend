package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Device;

public interface DeviceService {
    Iterable<Device> findAll();
    Optional<Device> findById(Integer id);
    List<Device> findByUserId(Integer userId);
    Device save(Device device);
    void deleteById(Integer id);
}
