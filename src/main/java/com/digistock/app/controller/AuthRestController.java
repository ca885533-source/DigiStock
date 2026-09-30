package com.digistock.app.controller;

import com.digistock.app.dto.LoginDTO;
import com.digistock.app.dto.RegistroUsuarioDTO;
import com.digistock.app.model.Usuario;
import com.digistock.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API REST de autenticacion del sistema DigiStock.
 *
 * A diferencia de AuthController (que devuelve paginas HTML con
 * Thymeleaf), este controlador devuelve unicamente JSON, pensado para
 * ser consumido por herramientas como Postman o por un futuro front-end
 * desacoplado (app movil, SPA, etc.).
 *
 * Servicios expuestos:
 *  - POST /api/auth/registro : registra un nuevo usuario.
 *  - POST /api/auth/login    : autentica un usuario existente.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UsuarioService usuarioService;

    public AuthRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Cuerpo esperado (JSON):
     * { "nombreUsuario": "...", "correo": "...", "nombreCompleto": "...",
     *   "contrasena": "...", "rol": "ADMINISTRADOR|VENDEDOR|ALMACENISTA" }
     *
     * Respuestas:
     *  - 200 OK: usuario registrado exitosamente.
     *  - 400 Bad Request: datos invalidos, o usuario/correo ya existente.
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody RegistroUsuarioDTO datos,
                                                           BindingResult result) {
        Map<String, Object> respuesta = new HashMap<>();

        if (result.hasErrors()) {
            respuesta.put("mensaje", "Datos invalidos");
            respuesta.put("errores", result.getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .toList());
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {
            Usuario usuario = usuarioService.registrar(datos);
            respuesta.put("mensaje", "Usuario registrado exitosamente");
            respuesta.put("usuario", usuario.getNombreUsuario());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException ex) {
            // Usuario o correo duplicado
            respuesta.put("mensaje", "Error al registrar usuario");
            respuesta.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Autentica un usuario existente.
     *
     * Cuerpo esperado (JSON): { "nombreUsuario": "...", "contrasena": "..." }
     *
     * Respuestas:
     *  - 200 OK: "Autenticacion satisfactoria", junto con el rol del usuario.
     *  - 400 Bad Request: "Error en la autenticacion" (usuario, contrasena
     *    incorrectos, o cuenta inactiva).
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO datos,
                                                       BindingResult result) {
        Map<String, Object> respuesta = new HashMap<>();

        if (result.hasErrors()) {
            respuesta.put("mensaje", "Error en la autenticacion");
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {
            Usuario usuario = usuarioService.autenticar(datos.getNombreUsuario(), datos.getContrasena());
            respuesta.put("mensaje", "Autenticacion satisfactoria");
            respuesta.put("usuario", usuario.getNombreUsuario());
            respuesta.put("rol", usuario.getRol());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException ex) {
            respuesta.put("mensaje", "Error en la autenticacion");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
}
