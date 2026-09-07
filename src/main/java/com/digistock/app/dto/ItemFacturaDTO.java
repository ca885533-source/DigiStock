package com.digistock.app.dto;

import lombok.Data;

/**
 * Objeto de transferencia de datos (DTO) para una linea de producto
 * dentro del formulario de creacion de factura (factura-form.html).
 *
 * Solo se envian desde el formulario el producto y la cantidad; el
 * precio, el nombre del producto y el subtotal se calculan y validan
 * en el servidor (FacturaService) a partir del precio real guardado en
 * la base de datos, nunca confiando en valores que pudieran venir
 * manipulados desde el navegador.
 */
@Data
public class ItemFacturaDTO {

    /** Id del producto seleccionado en la fila del formulario. */
    private String productoId;

    /** Cantidad de unidades que el vendedor desea facturar. */
    private int cantidad;
}
