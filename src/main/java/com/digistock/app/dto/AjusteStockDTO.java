package com.digistock.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada de PATCH /api/productos/{id}/stock.
 *
 * "cantidad" es un ajuste relativo: positivo para una entrada de
 * mercancia, negativo para una salida o correccion. El resultado
 * final nunca puede quedar por debajo de cero.
 */
@Data
public class AjusteStockDTO {

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}
