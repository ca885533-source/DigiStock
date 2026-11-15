package com.digistock.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para crear (POST) o actualizar (PUT) un producto.
 * Las reglas de validacion se evaluan con @Valid en el controlador.
 */
@Data
public class ProductoDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    private String nombre;

    @Positive(message = "El precio debe ser mayor que cero")
    private double precio;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    private int stockDisponible;
}
