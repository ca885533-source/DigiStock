package com.digistock.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad que representa un producto del inventario.
 *
 * Desde la evidencia AA5-EV03 este modelo cuenta con su propio modulo
 * completo de gestion (ProductoController para la vista web con
 * Thymeleaf, y ProductoRestController para la API JSON consumida desde
 * Postman u otros clientes externos).
 *
 * Se mapea a la coleccion "productos" en MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
public class Producto {

    @Id
    private String id;

    /** Nombre comercial del producto, visible en la factura. */
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    /** Precio unitario de venta (sin IVA). */
    @Positive(message = "El precio debe ser mayor que cero")
    private double precio;

    /** Unidades disponibles actualmente en bodega. */
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private int stockDisponible;
}
