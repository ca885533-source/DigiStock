package com.digistock.app.controller.api;

import com.digistock.app.dto.EstadoUsuarioDTO;
import com.digistock.app.dto.UsuarioRespuestaDTO;
import com.digistock.app.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicio REST de gestion de usuarios (solo ADMINISTRADOR).
 *
 * No existe DELETE: las cuentas se desactivan (activo=false) para conservar
 * el historial, decision de diseño ya tomada en el modulo de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Consulta y activacion/desactivacion de cuentas")
@SecurityRequirement(name = "basicAuth")
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public List<UsuarioRespuestaDTO> listar() {
        return usuarioService.listarTodos().stream().map(UsuarioRespuestaDTO::desde).toList();
    }

    @Operation(summary = "Consultar un usuario por su id")
    @GetMapping("/{id}")
    public UsuarioRespuestaDTO buscar(@PathVariable String id) {
        return UsuarioRespuestaDTO.desde(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Activar o desactivar una cuenta")
    @PatchMapping("/{id}/estado")
    public UsuarioRespuestaDTO cambiarEstado(@PathVariable String id,
                                             @Valid @RequestBody EstadoUsuarioDTO datos) {
        usuarioService.cambiarEstado(id, datos.getActivo());
        return UsuarioRespuestaDTO.desde(usuarioService.buscarPorId(id));
    }
}
