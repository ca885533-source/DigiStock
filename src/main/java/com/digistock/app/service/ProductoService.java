package com.digistock.app.service;

import com.digistock.app.model.Producto;
import com.digistock.app.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio del modulo de inventario.
 *
 * Centraliza las reglas de creacion, edicion y eliminacion de productos
 * para que tanto el modulo web (ProductoController) como la API REST
 * (ProductoRestController) reutilicen exactamente el mismo
 * comportamiento, en vez de duplicar la logica en los dos controladores.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Lista todos los productos del inventario, ordenados por nombre. */
    public List<Producto> listarTodos() {
        return productoRepository.findAll(org.springframework.data.domain.Sort.by("nombre"));
    }

    /** Busca un producto por su id; lanza error si no existe. */
    public Producto buscarPorId(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    /** Registra un nuevo producto en el inventario. */
    public Producto crear(Producto producto) {
        producto.setId(null); // se asegura que sea un producto nuevo, no una sobrescritura
        return productoRepository.save(producto);
    }

    /** Actualiza el nombre, precio y stock de un producto existente. */
    public Producto actualizar(String id, Producto datos) {
        Producto producto = buscarPorId(id);
        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setStockDisponible(datos.getStockDisponible());
        return productoRepository.save(producto);
    }

    /** Elimina un producto del inventario. */
    public void eliminar(String id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }
}
