package com.digistock.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO usado por la API REST de autenticacion (POST /api/auth/login).
 *
 * Se recibe el usuario y la contrasena en texto plano desde el cliente
 * (Postman, o cualquier front-end), y se comparan contra el hash
 * guardado en la base de datos usando BCrypt (ver UsuarioService.autenticar).
 */
@Data
public class LoginDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;
}
