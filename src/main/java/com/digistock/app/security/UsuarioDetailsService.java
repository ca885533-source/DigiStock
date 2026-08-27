package com.digistock.app.security;

import com.digistock.app.model.Usuario;
import com.digistock.app.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementacion de UserDetailsService requerida por Spring Security.
 *
 * Cuando un usuario intenta iniciar sesion, Spring Security invoca
 * loadUserByUsername() para obtener sus credenciales y su rol desde
 * MongoDB, y luego compara la contrasena ingresada contra el hash
 * guardado usando el PasswordEncoder configurado en SecurityConfig.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con el nombre: " + nombreUsuario));
        return new UsuarioPrincipal(usuario);
    }
}
