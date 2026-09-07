package com.digistock.app;

import com.digistock.app.dto.FacturaDTO;
import com.digistock.app.dto.ItemFacturaDTO;
import com.digistock.app.model.EstadoFactura;
import com.digistock.app.model.Factura;
import com.digistock.app.model.Producto;
import com.digistock.app.repository.FacturaRepository;
import com.digistock.app.repository.ProductoRepository;
import com.digistock.app.service.FacturaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del modulo de facturacion.
 *
 * Cubren las reglas de negocio definidas en FacturaService: calculo
 * correcto de subtotal/IVA/total, descuento de stock al vender,
 * rechazo de ventas sin stock suficiente y reintegro de stock al anular.
 *
 * Se usan repositorios simulados (mock) para no depender de una base de
 * datos MongoDB real durante las pruebas, igual que en UsuarioServiceTest.
 */
class FacturaServiceTest {

    private FacturaRepository facturaRepository;
    private ProductoRepository productoRepository;
    private FacturaService facturaService;

    @BeforeEach
    void configurar() {
        facturaRepository = mock(FacturaRepository.class);
        productoRepository = mock(ProductoRepository.class);
        facturaService = new FacturaService(facturaRepository, productoRepository);
    }

    @Test
    void debeCalcularSubtotalIvaYTotalCorrectamente() {
        Producto producto = new Producto("p1", "Camiseta", 100.0, 10);
        when(productoRepository.findById("p1")).thenReturn(Optional.of(producto));
        when(facturaRepository.count()).thenReturn(0L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        FacturaDTO dto = new FacturaDTO();
        dto.setNombreCliente("Cliente Uno");
        dto.setDocumentoCliente("123456");
        ItemFacturaDTO item = new ItemFacturaDTO();
        item.setProductoId("p1");
        item.setCantidad(2);
        dto.setItems(List.of(item));

        Factura resultado = facturaService.crear(dto, "vendedor1");

        assertEquals(200.0, resultado.getSubtotal(), 0.001);
        assertEquals(38.0, resultado.getIva(), 0.001, "El IVA debe ser el 19% del subtotal");
        assertEquals(238.0, resultado.getTotal(), 0.001);
        assertEquals(EstadoFactura.PAGADA, resultado.getEstado());
    }

    @Test
    void debeDescontarElStockVendido() {
        Producto producto = new Producto("p1", "Camiseta", 100.0, 10);
        when(productoRepository.findById("p1")).thenReturn(Optional.of(producto));
        when(facturaRepository.count()).thenReturn(0L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        FacturaDTO dto = new FacturaDTO();
        dto.setNombreCliente("Cliente Uno");
        dto.setDocumentoCliente("123456");
        ItemFacturaDTO item = new ItemFacturaDTO();
        item.setProductoId("p1");
        item.setCantidad(3);
        dto.setItems(List.of(item));

        facturaService.crear(dto, "vendedor1");

        assertEquals(7, producto.getStockDisponible(), "Debe quedar 10 - 3 = 7 unidades disponibles");
        verify(productoRepository).save(producto);
    }

    @Test
    void debeRechazarVentaSinStockSuficiente() {
        Producto producto = new Producto("p1", "Camiseta", 100.0, 2);
        when(productoRepository.findById("p1")).thenReturn(Optional.of(producto));

        FacturaDTO dto = new FacturaDTO();
        dto.setNombreCliente("Cliente Uno");
        dto.setDocumentoCliente("123456");
        ItemFacturaDTO item = new ItemFacturaDTO();
        item.setProductoId("p1");
        item.setCantidad(5);
        dto.setItems(List.of(item));

        assertThrows(IllegalArgumentException.class, () -> facturaService.crear(dto, "vendedor1"));
        verify(facturaRepository, never()).save(any());
    }

    @Test
    void debeReintegrarStockAlAnularUnaFactura() {
        Producto producto = new Producto("p1", "Camiseta", 100.0, 5);
        var item = new com.digistock.app.model.ItemFactura("p1", "Camiseta", 3, 100.0, 300.0);
        Factura factura = new Factura();
        factura.setId("f1");
        factura.setItems(List.of(item));
        factura.setEstado(EstadoFactura.PAGADA);

        when(facturaRepository.findById("f1")).thenReturn(Optional.of(factura));
        when(productoRepository.findById("p1")).thenReturn(Optional.of(producto));

        facturaService.anular("f1");

        assertEquals(8, producto.getStockDisponible(), "Debe reintegrarse el stock vendido (5 + 3 = 8)");
        assertEquals(EstadoFactura.ANULADA, factura.getEstado());
        verify(facturaRepository).save(factura);
    }
}
