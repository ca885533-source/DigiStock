package com.digistock.app.dto;

import com.digistock.app.model.Usuario;

import java.time.LocalDateTime;

/**
 * DTO de salida para usuarios.
 *
 * Nunca incluye la contrasena (ni siquiera encriptada): la API solo
 * expone los datos que el cliente necesita conocer.
 */
public record UsuarioRespuestaDTO(
        String id,
        String nombreUsuario,
        String correo,
        String nombreCompleto,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion) {

    /** Convierte la entidad Usuario en su representacion segura para la API. */
    public static UsuarioRespuestaDTO desde(Usuario u) {
        return new UsuarioRespuestaDTO(
                u.getId(),
                u.getNombreUsuario(),
                u.getCorreo(),
                u.getNombreCompleto(),
                u.getRol() == null ? null : u.getRol().name(),
                u.isActivo(),
                u.getFechaCreacion());
    }
}
