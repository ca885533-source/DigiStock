package com.digistock.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Objeto de transferencia de datos (DTO) usado por el formulario de registro
 * de usuarios (registro.html).
 *
 * Aqui se definen las reglas de validacion de la historia de usuario
 * "Como administrador quiero registrar usuarios con rol asignado,
 * validando los datos ingresados, para controlar el acceso al sistema".
 *
 * Estas anotaciones son evaluadas automaticamente por Spring antes de que
 * el controlador procese la peticion (ver AuthController.registrarUsuario).
 */
@Data
public class RegistroUsuarioDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo admite letras, numeros y guion bajo")
    private String nombreUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    private String correo;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre completo debe tener entre 3 y 60 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 30, message = "La contrasena debe tener entre 8 y 30 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "La contrasena debe incluir al menos una letra y un numero"
    )
    private String contrasena;

    @NotBlank(message = "Debe seleccionar un rol")
    private String rol;
}
