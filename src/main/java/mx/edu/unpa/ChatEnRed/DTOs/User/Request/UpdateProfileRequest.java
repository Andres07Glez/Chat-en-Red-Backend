package mx.edu.unpa.ChatEnRed.DTOs.User.Request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String bio;
}
