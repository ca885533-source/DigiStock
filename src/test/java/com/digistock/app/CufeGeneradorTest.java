package com.digistock.app;

import com.digistock.app.model.Factura;
import com.digistock.app.service.CufeGenerador;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del CUFE (codigo unico de la factura electronica simulada):
 * debe ser estable para los mismos datos y cambiar si los datos cambian.
 */
class CufeGeneradorTest {

    private Factura facturaDeEjemplo() {
        Factura factura = new Factura();
        factura.setNumeroFactura("FAC-000001");
        factura.setFecha(LocalDateTime.of(2026, 11, 13, 10, 30, 0));
        factura.setVendedor("vendedor1");
        factura.setDocumentoCliente("123456");
        factura.setSubtotal(200.0);
        factura.setIva(38.0);
        factura.setTotal(238.0);
        return factura;
    }

    @Test
    void debeGenerarCodigoHexadecimalDe96Caracteres() {
        String cufe = CufeGenerador.generar(facturaDeEjemplo());
        assertEquals(96, cufe.length());
        assertTrue(cufe.matches("[0-9a-f]+"));
    }

    @Test
    void mismosDatosDebenProducirMismoCodigo() {
        assertEquals(CufeGenerador.generar(facturaDeEjemplo()),
                CufeGenerador.generar(facturaDeEjemplo()));
    }

    @Test
    void cambiarElTotalDebeCambiarElCodigo() {
        Factura modificada = facturaDeEjemplo();
        modificada.setTotal(999.0);
        assertNotEquals(CufeGenerador.generar(facturaDeEjemplo()), CufeGenerador.generar(modificada));
    }
}
