package com.digistock.app;

import com.digistock.app.model.Rol;
import com.digistock.app.model.Usuario;
import com.digistock.app.repository.UsuarioRepository;
import com.digistock.app.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de login de la API (POST /api/auth/login).
 */
class AutenticacionServiceTest {

    private UsuarioRepository repositorio;
    private BCryptPasswordEncoder codificador;
    private UsuarioService servicio;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        repositorio = mock(UsuarioRepository.class);
        codificador = new BCryptPasswordEncoder();
        servicio = new UsuarioService(repositorio, codificador);

        usuario = new Usuario();
        usuario.setNombreUsuario("vendedor1");
        usuario.setContrasena(codificador.encode("Clave1234"));
        usuario.setRol(Rol.VENDEDOR);
        usuario.setActivo(true);
        when(repositorio.findByNombreUsuario("vendedor1")).thenReturn(Optional.of(usuario));
    }

    @Test
    void credencialesCorrectasDevuelvenElUsuario() {
        assertEquals("vendedor1", servicio.autenticar("vendedor1", "Clave1234").getNombreUsuario());
    }

    @Test
    void contrasenaIncorrectaLanzaCredencialesInvalidas() {
        assertThrows(BadCredentialsException.class, () -> servicio.autenticar("vendedor1", "otraClave1"));
    }

    @Test
    void usuarioInexistenteLanzaCredencialesInvalidas() {
        when(repositorio.findByNombreUsuario("nadie")).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> servicio.autenticar("nadie", "Clave1234"));
    }

    @Test
    void cuentaDesactivadaNoPuedeIniciarSesion() {
        usuario.setActivo(false);
        assertThrows(BadCredentialsException.class, () -> servicio.autenticar("vendedor1", "Clave1234"));
    }
}
