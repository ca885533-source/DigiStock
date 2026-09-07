package com.digistock.app.config;

import com.digistock.app.model.Producto;
import com.digistock.app.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Carga un catalogo minimo de productos de ejemplo al arrancar la
 * aplicacion, unicamente si la coleccion "productos" esta vacia.
 *
 * NOTA DE ALCANCE: el modulo de inventario (CRUD completo de productos,
 * entradas/salidas de stock) se desarrolla en otra evidencia del
 * proyecto formativo. Este sembrador de datos existe solo para que el
 * modulo de facturacion se pueda probar de forma independiente mientras
 * tanto, sin depender de que el modulo de inventario ya este listo.
 */
@Configuration
public class DatosIniciales {

    @org.springframework.context.annotation.Bean
    public CommandLineRunner cargarProductosDemo(ProductoRepository productoRepository) {
        return args -> {
            if (productoRepository.count() == 0) {
                productoRepository.save(new Producto(null, "Camiseta basica", 35000, 50));
                productoRepository.save(new Producto(null, "Pantalon jean", 89000, 30));
                productoRepository.save(new Producto(null, "Chaqueta impermeable", 150000, 15));
                productoRepository.save(new Producto(null, "Gorra deportiva", 25000, 40));
                productoRepository.save(new Producto(null, "Zapatos casuales", 120000, 20));
            }
        };
    }
}
