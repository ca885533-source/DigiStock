package com.digistock.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada de PATCH /api/usuarios/{id}/estado.
 * Indica si la cuenta debe quedar activa (true) o desactivada (false).
 */
@Data
public class EstadoUsuarioDTO {

    @NotNull(message = "El campo activo es obligatorio (true o false)")
    private Boolean activo;
}
