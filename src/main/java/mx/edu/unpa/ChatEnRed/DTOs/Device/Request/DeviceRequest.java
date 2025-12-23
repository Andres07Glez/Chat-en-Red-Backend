package mx.edu.unpa.ChatEnRed.DTOs.Device.Request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DeviceRequest {
    private Integer userId;
    private String deviceName;
    private String publicKey;
    private LocalDateTime createdAt; 