package com.digistock.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad que representa un producto del inventario.
 *
 * NOTA DE ALCANCE: el modulo completo de inventario (entradas/salidas,
 * categorias, proveedores) se desarrolla en otra evidencia. Aqui se
 * define unicamente lo minimo necesario para que el modulo de
 * facturacion pueda seleccionar productos y descontar stock al vender.
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
    private String nombre;

    /** Precio unitario de venta (sin IVA). */
    private double precio;

    /** Unidades disponibles actualmente en bodega. */
    private int stockDisponible;
}
