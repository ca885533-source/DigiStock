package com.digistock.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una linea (item) dentro de una factura: un producto vendido,
 * la cantidad y el precio unitario en el momento de la venta.
 *
 * No es una coleccion propia de MongoDB: se guarda embebida dentro del
 * documento Factura, siguiendo el modelo de datos NoSQL orientado a
 * documentos (evita relaciones tipo join, todo lo que pertenece a una
 * factura viaja junto con ella).
 *
 * Se guarda el precio unitario copiado en el momento de la venta (y no
 * una referencia al precio actual del producto) para que, si el precio
 * del producto cambia despues, las facturas historicas no se alteren.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFactura {

    /** Referencia al producto vendido (id de la coleccion "productos"). */
    private String productoId;

    /** Nombre del producto copiado al momento de la venta (para mostrar en la factura). */
    private String nombreProducto;

    /** Cantidad de unidades vendidas. */
    private int cantidad;

    /** Precio unitario vigente en el momento de la venta. */
    private double precioUnitario;

    /** Subtotal de la linea: cantidad * precioUnitario. */
    private double subtotal;
}
