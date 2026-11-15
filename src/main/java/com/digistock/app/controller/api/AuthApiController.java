package com.digistock.app.controller.api;

import com.digistock.app.dto.LoginDTO;
import com.digistock.app.dto.RegistroUsuarioDTO;
import com.digistock.app.dto.UsuarioRespuestaDTO;
import com.digistock.app.model.Rol;
import com.digistock.app.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Servicio REST de autenticacion: login y registro de usuarios.
 *
 * Historias de usuario cubiertas:
 *  - Como usuario quiero iniciar sesion para acceder segun mi rol.
 *  - Como administrador quiero registrar usuarios asignandoles un rol.
 *
 * Nota: el registro por API exige rol ADMINISTRADOR (ver ApiSecurityConfig).
 * El primer administrador se crea desde el formulario web /registro.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Login y registro de usuarios")
public class AuthApiController {

    private final UsuarioService usuarioService;

    public AuthApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Iniciar sesion (verifica usuario y contrasena)")
    @PostMapping("/login")
    public UsuarioRespuestaDTO login(@Valid @RequestBody LoginDTO datos) {
        return UsuarioRespuestaDTO.desde(
                usuarioService.autenticar(datos.getNombreUsuario(), datos.getContrasena()));
    }

    @Operation(summary = "Registrar un usuario nuevo (solo ADMINISTRADOR)")
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDTO> registro(@Valid @RequestBody RegistroUsuarioDTO datos) {
        // Se valida el rol aqui para devolver un mensaje claro en vez del
        // error tecnico de Enum.valueOf.
        boolean rolValido = Arrays.stream(Rol.values()).anyMatch(r -> r.name().equals(datos.getRol()));
        if (!rolValido) {
            throw new IllegalArgumentException("Rol invalido. Valores permitidos: "
                    + Arrays.toString(Rol.values()));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioRespuestaDTO.desde(usuarioService.registrar(datos)));
    }
}
