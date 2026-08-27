package com.digistock.app.controller;

import com.digistock.app.dto.RegistroUsuarioDTO;
import com.digistock.app.model.Rol;
import com.digistock.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador encargado de las vistas y peticiones de autenticacion:
 * pantalla de inicio de sesion y registro de nuevos usuarios.
 *
 * Historias de usuario cubiertas:
 *  - HU: "Como usuario quiero iniciar sesion con mi usuario y contrasena
 *         para acceder al sistema segun mi rol".
 *  - HU: "Como administrador quiero registrar nuevos usuarios asignandoles
 *         un rol, validando que los datos sean correctos".
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Muestra el formulario de inicio de sesion. */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    /** Muestra el formulario de registro con un DTO vacio y la lista de roles. */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroUsuarioDTO", new RegistroUsuarioDTO());
        model.addAttribute("roles", Rol.values());
        return "registro";
    }

    /**
     * Procesa el formulario de registro.
     *
     * @Valid activa las validaciones definidas en RegistroUsuarioDTO. Si algo
     * falla (campo vacio, correo invalido, contrasena corta, etc.), Spring
     * llena "result" con los errores y se vuelve a mostrar el formulario
     * con los mensajes correspondientes, sin tocar la base de datos.
     */
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute RegistroUsuarioDTO registroUsuarioDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "registro";
        }

        try {
            usuarioService.registrar(registroUsuarioDTO);
        } catch (IllegalArgumentException ex) {
            // Errores de negocio (usuario o correo duplicado)
            model.addAttribute("errorNegocio", ex.getMessage());
            model.addAttribute("roles", Rol.values());
            return "registro";
        }

        return "redirect:/login?registrado=true";
    }
}
