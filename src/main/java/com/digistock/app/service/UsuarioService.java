package com.digistock.app.service;

import com.digistock.app.dto.RegistroUsuarioDTO;
import com.digistock.app.model.Rol;
import com.digistock.app.model.Usuario;
import com.digistock.app.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que contiene la logica de negocio del modulo de usuarios.
 *
 * Se mantiene separado del controlador (patron MVC + capa de servicio)
 * para que las reglas de negocio (validar duplicados, encriptar
 * contrasenas, asignar rol) no queden mezcladas con el manejo HTTP.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param datos datos validados provenientes del formulario de registro
     * @return el usuario creado
     * @throws IllegalArgumentException si el nombre de usuario o el correo ya existen
     */
    public Usuario registrar(RegistroUsuarioDTO datos) {
        if (usuarioRepository.existsByNombreUsuario(datos.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso");
        }
        if (usuarioRepository.existsByCorreo(datos.getCorreo())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(datos.getNombreUsuario());
        usuario.setCorreo(datos.getCorreo());
        usuario.setNombreCompleto(datos.getNombreCompleto());
        // La contrasena NUNCA se guarda en texto plano: se encripta con BCrypt
        usuario.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        usuario.setRol(Rol.valueOf(datos.getRol()));
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    /** Lista todos los usuarios registrados (usado en el panel de administracion). */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /** Activa o desactiva una cuenta sin eliminarla de la base de datos. */
    public void cambiarEstado(String id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }
}
