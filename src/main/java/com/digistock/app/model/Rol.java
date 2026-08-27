package com.digistock.app.model;

/**
 * Roles disponibles dentro del sistema DigiStock.
 *
 * Cada rol determina que modulos y funcionalidades puede usar el usuario:
 *  - ADMINISTRADOR: acceso total (usuarios, inventario, facturacion, reportes).
 *  - VENDEDOR: puede crear facturas y consultar inventario.
 *  - ALMACENISTA: puede gestionar el inventario (entradas/salidas de stock).
 */
public enum Rol {
    ADMINISTRADOR,
    VENDEDOR,
    ALMACENISTA
}
