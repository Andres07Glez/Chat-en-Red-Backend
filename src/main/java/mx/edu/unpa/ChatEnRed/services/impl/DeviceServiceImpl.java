package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import mx.edu.unpa.ChatEnRed.DTOs.Device.Request.DeviceRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Device.Response.DeviceResponse;
import mx.edu.unpa.ChatEnRed.domains.Device;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.DeviceMapper;
import mx.edu.unpa.ChatEnRed.repositories.DeviceRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.DeviceService;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> findAll() {
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeviceResponse> findById(Integer id) {
        return deviceRepository.findById(id)
                .map(deviceMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<DeviceResponse> save(DeviceRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Device device = deviceMapper.toEntity(request, user);

        return Optional.of(device)
        		.map(deviceRepository::save)
        		.map(deviceMapper::toResponse);
        
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return deviceRepository.findById(id)
                .map(entity -> {
                    deviceRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<DeviceResponse> update(Integer id, DeviceRequest request) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        existing.setUser(user);
        existing.setDeviceName(request.getDeviceName());
        existing.setPublicKey(request.getPublicKey());
        // if (request.getCreatedAt() != null) existing.setCreatedAt(request.getCreatedAt());

        return Optional.of(existing)
        		.map(deviceRepository::save)
        		.map(deviceMapper::toResponse);
    }
}