package com.digistock.app.controller;

import com.digistock.app.security.UsuarioPrincipal;
import com.digistock.app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador del panel principal (dashboard) y de la administracion de
 * usuarios existentes. El acceso a "/usuarios/**" ya esta restringido al
 * rol ADMINISTRADOR desde SecurityConfig.
 */
@Controller
public class DashboardController {

    private final UsuarioService usuarioService;

    public DashboardController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Pantalla principal despues de iniciar sesion; saluda segun el rol del usuario. */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("usuario", principal.getUsuario());
        return "dashboard";
    }

    /** Lista todos los usuarios del sistema (solo ADMINISTRADOR). */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios";
    }

    /** Activa o desactiva la cuenta de un usuario (solo ADMINISTRADOR). */
    @PostMapping("/usuarios/{id}/estado")
    public String cambiarEstado(@PathVariable String id, @org.springframework.web.bind.annotation.RequestParam boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        return "redirect:/usuarios";
    }
}
