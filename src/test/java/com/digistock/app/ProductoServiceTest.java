package com.digistock.app;

import com.digistock.app.dto.ProductoDTO;
import com.digistock.app.model.Producto;
import com.digistock.app.repository.ProductoRepository;
import com.digistock.app.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de inventario de productos (API REST).
 * Se usa un repositorio simulado para no depender de MongoDB.
 */
class ProductoServiceTest {

    private ProductoRepository repositorio;
    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(ProductoRepository.class);
        servicio = new ProductoService(repositorio);
        // save devuelve el mismo objeto que recibe, como haria MongoDB
        when(repositorio.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private ProductoDTO dto(String nombre, double precio, int stock) {
        ProductoDTO d = new ProductoDTO();
        d.setNombre(nombre);
        d.setPrecio(precio);
        d.setStockDisponible(stock);
        return d;
    }

    @Test
    void crearGuardaElProductoConLosDatosRecibidos() {
        Producto creado = servicio.crear(dto("  Bufanda  ", 18000, 10));

        assertEquals("Bufanda", creado.getNombre()); // se recortan espacios
        assertEquals(18000, creado.getPrecio());
        assertEquals(10, creado.getStockDisponible());
    }

    @Test
    void ajustarStockSumaUnaEntradaDeMercancia() {
        when(repositorio.findById("p1")).thenReturn(Optional.of(new Producto("p1", "Gorra", 25000, 40)));

        Producto actualizado = servicio.ajustarStock("p1", 15);

        assertEquals(55, actualizado.getStockDisponible());
    }

    @Test
    void ajustarStockRestaUnaSalidaDeMercancia() {
        when(repositorio.findById("p1")).thenReturn(Optional.of(new Producto("p1", "Gorra", 25000, 40)));

        assertEquals(30, servicio.ajustarStock("p1", -10).getStockDisponible());
    }

    @Test
    void ajustarStockRechazaDejarElInventarioEnNegativo() {
        when(repositorio.findById("p1")).thenReturn(Optional.of(new Producto("p1", "Gorra", 25000, 5)));

        assertThrows(IllegalArgumentException.class, () -> servicio.ajustarStock("p1", -6));
        verify(repositorio, never()).save(any());
    }

    @Test
    void ajustarStockRechazaCantidadCero() {
        assertThrows(IllegalArgumentException.class, () -> servicio.ajustarStock("p1", 0));
    }

    @Test
    void buscarProductoInexistenteLanzaExcepcionDeNoEncontrado() {
        when(repositorio.findById("x")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.buscarPorId("x"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrad"));
    }

    @Test
    void eliminarProductoInexistenteNoBorraNada() {
        when(repositorio.findById("x")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar("x"));
        verify(repositorio, never()).deleteById(any());
    }
}
