package com.pixelcraft.backend.dto;

import com.pixelcraft.backend.model.Rol;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Rol rol; // ADMIN, VENDEDOR o CLIENTE
}