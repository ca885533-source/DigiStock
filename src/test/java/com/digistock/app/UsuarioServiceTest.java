package com.digistock.app;

import com.digistock.app.dto.RegistroUsuarioDTO;
import com.digistock.app.model.Usuario;
import com.digistock.app.repository.UsuarioRepository;
import com.digistock.app.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del modulo de autenticacion/usuarios.
 *
 * Cubren las validaciones de negocio exigidas por la historia de usuario
 * de registro: no permitir usuarios ni correos duplicados, y encriptar
 * la contrasena antes de guardarla.
 *
 * Se usa un repositorio simulado (mock) para no depender de una base de
 * datos MongoDB real durante las pruebas.
 */
class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioService usuarioService;

    @BeforeEach
    void configurar() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioService = new UsuarioService(usuarioRepository, new BCryptPasswordEncoder());
    }

    @Test
    void debeRegistrarUsuarioCorrectamenteYEncriptarLaContrasena() {
        RegistroUsuarioDTO datos = new RegistroUsuarioDTO();
        datos.setNombreUsuario("jdoe");
        datos.setCorreo("jdoe@digistock.com");
        datos.setNombreCompleto("John Doe");
        datos.setContrasena("clave123");
        datos.setRol("VENDEDOR");

        when(usuarioRepository.existsByNombreUsuario("jdoe")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("jdoe@digistock.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrar(datos);

        assertNotEquals("clave123", resultado.getContrasena(), "La contrasena no debe guardarse en texto plano");
        assertEquals("jdoe", resultado.getNombreUsuario());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void debeRechazarNombreDeUsuarioDuplicado() {
        RegistroUsuarioDTO datos = new RegistroUsuarioDTO();
        datos.setNombreUsuario("jdoe");
        datos.setCorreo("otro@digistock.com");
        datos.setNombreCompleto("John Doe");
        datos.setContrasena("clave123");
        datos.setRol("VENDEDOR");

        when(usuarioRepository.existsByNombreUsuario("jdoe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrar(datos));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeRechazarCorreoDuplicado() {
        RegistroUsuarioDTO datos = new RegistroUsuarioDTO();
        datos.setNombreUsuario("nuevo");
        datos.setCorreo("jdoe@digistock.com");
        datos.setNombreCompleto("John Doe");
        datos.setContrasena("clave123");
        datos.setRol("ALMACENISTA");

        when(usuarioRepository.existsByNombreUsuario("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("jdoe@digistock.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrar(datos));
        verify(usuarioRepository, never()).save(any());
    }
}
