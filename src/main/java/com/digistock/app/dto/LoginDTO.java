package com.digistock.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada del servicio POST /api/auth/login.
 * Solo lleva las credenciales que el cliente envia en el cuerpo JSON.
 */
@Data
public class LoginDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;
}
