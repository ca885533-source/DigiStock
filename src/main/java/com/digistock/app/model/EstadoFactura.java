package com.digistock.app.model;

/**
 * Estados posibles de una factura dentro del modulo de facturacion.
 *
 *  - PAGADA: la venta se realizo correctamente y afecto el stock.
 *  - ANULADA: la factura fue cancelada; no se contabiliza en reportes
 *             de ventas, pero se conserva para trazabilidad (no se borra).
 */
public enum EstadoFactura {
    PAGADA,
    ANULADA
}
