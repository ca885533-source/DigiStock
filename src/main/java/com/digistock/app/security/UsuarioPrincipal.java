package com.digistock.app.security;

import com.digistock.app.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador que envuelve la entidad Usuario para que Spring Security
 * pueda usarla dentro de su mecanismo de autenticacion (UserDetails).
 *
 * El rol se expone con el prefijo "ROLE_" porque asi lo exige el
 * estandar interno de Spring Security al evaluar hasRole().
 */
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    /** Permite recuperar el usuario original desde el controlador si se necesita. */
    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // La cuenta solo esta habilitada si el usuario esta marcado como activo
        return usuario.isActivo();
    }
}
