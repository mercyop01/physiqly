package com.physiqly.userservice;

import lombok.Data;

// This is a Data Transfer Object (DTO).
// It's a simple class used only to transfer data between the frontend and backend.
@Data
public class LoginRequest {
    private String email;
    private String password;
}
