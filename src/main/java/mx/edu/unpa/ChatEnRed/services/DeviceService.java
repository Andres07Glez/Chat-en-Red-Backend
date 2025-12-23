package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Device.Request.DeviceRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Device.Response.DeviceResponse;

public interface DeviceService {
    List<DeviceResponse> findAll();
    Optional<DeviceResponse> findById(Integer id);
    Optional<DeviceResponse> save(DeviceRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<DeviceResponse> update(Integer id, DeviceRequest request);
}