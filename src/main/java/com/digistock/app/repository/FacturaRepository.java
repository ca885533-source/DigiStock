package com.digistock.app.repository;

import com.digistock.app.model.Factura;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la coleccion "facturas" en MongoDB.
 *
 * Al extender MongoRepository se obtienen automaticamente las operaciones
 * basicas (guardar, buscar por id, listar, contar) y se declaran consultas
 * derivadas del nombre del metodo para las busquedas propias del modulo.
 */
public interface FacturaRepository extends MongoRepository<Factura, String> {

    /** Lista las facturas ordenadas de la mas reciente a la mas antigua. */
    List<Factura> findAllByOrderByFechaDesc();
}
