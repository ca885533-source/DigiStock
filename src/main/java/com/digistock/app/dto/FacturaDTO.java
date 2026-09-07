package com.digistock.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que respalda el formulario de creacion de una factura
 * (factura-form.html).
 *
 * Cubre la historia de usuario:
 *  - HU: "Como vendedor quiero ingresar los datos del cliente y los
 *         productos vendidos para generar una factura valida".
 *
 * La lista "items" se llena dinamicamente en el front-end con
 * JavaScript (boton "Agregar producto"); Spring la vincula usando el
 * indice de cada fila en el nombre del campo HTML (items[0].productoId,
 * items[1].productoId, etc.).
 */
@Data
public class FacturaDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotBlank(message = "El documento del cliente es obligatorio")
    private String documentoCliente;

    @NotEmpty(message = "Debe agregar al menos un producto a la factura")
    @Valid
    private List<ItemFacturaDTO> items = new ArrayList<>();
}
