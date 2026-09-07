package com.digistock.app.repository;

import com.digistock.app.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio de acceso a datos para la coleccion "productos" en MongoDB.
 * Usado por el modulo de facturacion para consultar precio y stock
 * disponible al momento de generar una venta.
 */
public interface ProductoRepository extends MongoRepository<Producto, String> {
}
