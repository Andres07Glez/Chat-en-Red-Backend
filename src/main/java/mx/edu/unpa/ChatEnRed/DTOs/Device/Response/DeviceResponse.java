package mx.edu.unpa.ChatEnRed.DTOs.Device.Response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DeviceResponse {
    private Integer id;
    private Integer userId;
    private String deviceName;
    private String publicKey;
    private LocalDateTime createdAt;
}