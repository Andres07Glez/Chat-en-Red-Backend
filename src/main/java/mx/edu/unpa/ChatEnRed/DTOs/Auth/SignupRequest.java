package mx.edu.unpa.ChatEnRed.DTOs.Auth;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
}
