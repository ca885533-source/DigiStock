package com.digistock.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una factura de venta.
 *
 * Se mapea a la coleccion "facturas" en MongoDB. Corresponde al modulo
 * de facturacion definido en el diagrama de clases del proyecto DigiStock:
 * una factura tiene datos del cliente, del vendedor que la genera, una
 * lista de items vendidos y los totales calculados (subtotal, IVA, total).
 *
 * Historia de usuario cubierta:
 *  - HU: "Como vendedor quiero registrar una venta seleccionando productos
 *         y cantidades, para generar una factura con el total a cobrar".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "facturas")
public class Factura {

    @Id
    private String id;

    /** Numero consecutivo de la factura, visible al cliente (ej: FAC-000001). */
    private String numeroFactura;

    /** Fecha y hora en que se genero la factura. */
    private LocalDateTime fecha = LocalDateTime.now();

    /** Nombre de usuario del vendedor que registro la venta. */
    private String vendedor;

    /** Nombre del cliente al que se le factura. */
    private String nombreCliente;

    /** Documento de identidad del cliente. */
    private String documentoCliente;

    /** Lineas de productos vendidos (ver ItemFactura). */
    private List<ItemFactura> items;

    /** Suma de los subtotales de todos los items (sin IVA). */
    private double subtotal;

    /** Valor del IVA calculado sobre el subtotal (19%). */
    private double iva;

    /** Total a cobrar: subtotal + iva. */
    private double total;

    /** Estado actual de la factura (ver enum EstadoFactura). */
    private EstadoFactura estado = EstadoFactura.PAGADA;
}
