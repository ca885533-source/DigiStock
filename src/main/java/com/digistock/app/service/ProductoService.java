package com.digistock.app.service;

import com.digistock.app.dto.ProductoDTO;
import com.digistock.app.model.Producto;
import com.digistock.app.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio con la logica de negocio del inventario de productos.
 *
 * Complementa al modulo de facturacion: FacturaService descuenta el stock
 * al vender; este servicio permite crear productos, corregir precios y
 * registrar entradas o salidas de mercancia.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Lista todos los productos del inventario. */
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    /** Busca un producto por id. @throws IllegalArgumentException si no existe */
    public Producto buscarPorId(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    /** Crea un producto nuevo a partir de los datos validados. */
    public Producto crear(ProductoDTO datos) {
        Producto producto = new Producto();
        copiarDatos(datos, producto);
        return productoRepository.save(producto);
    }

    /** Reemplaza nombre, precio y stock de un producto existente. */
    public Producto actualizar(String id, ProductoDTO datos) {
        Producto producto = buscarPorId(id);
        copiarDatos(datos, producto);
        return productoRepository.save(producto);
    }

    /**
     * Registra una entrada (cantidad positiva) o salida (cantidad negativa)
     * de mercancia.
     *
     * @throws IllegalArgumentException si la cantidad es cero o si el
     *         ajuste dejaria el stock en negativo
     */
    public Producto ajustarStock(String id, int cantidad) {
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad del ajuste no puede ser cero");
        }
        Producto producto = buscarPorId(id);
        int nuevoStock = producto.getStockDisponible() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                    "Stock insuficiente: el inventario no puede quedar en negativo. Disponible: "
                            + producto.getStockDisponible());
        }
        producto.setStockDisponible(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto. Las facturas ya emitidas no se ven afectadas
     * porque guardan una copia del nombre y del precio del item.
     */
    public void eliminar(String id) {
        buscarPorId(id);
        productoRepository.deleteById(id);
    }

    private void copiarDatos(ProductoDTO origen, Producto destino) {
        destino.setNombre(origen.getNombre().trim());
        destino.setPrecio(origen.getPrecio());
        destino.setStockDisponible(origen.getStockDisponible());
    }
}
