package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Device;

public interface DeviceService {
    Iterable<Device> findAll();
    Optional<Device> findById(int id);
    List<Device> findByUserId(int userId);
    Device save(Device device);
    void deleteById(int id);
}
