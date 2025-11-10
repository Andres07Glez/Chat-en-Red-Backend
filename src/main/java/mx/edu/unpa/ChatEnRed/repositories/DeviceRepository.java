package mx.edu.unpa.ChatEnRed.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.unpa.ChatEnRed.domains.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    List<Device> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
