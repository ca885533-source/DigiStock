package com.digistock.app.controller;

import com.digistock.app.dto.FacturaDTO;
import com.digistock.app.model.Factura;
import com.digistock.app.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST del modulo de facturacion del sistema DigiStock.
 *
 * Reutiliza toda la logica de negocio ya implementada en FacturaService
 * (calculo de IVA, descuento de stock, generacion de numero consecutivo,
 * reintegro de stock al anular), exponiendola ahora como servicios JSON
 * en lugar de vistas Thymeleaf.
 *
 * Servicios expuestos:
 *  - GET  /api/facturas             : lista todas las facturas.
 *  - GET  /api/facturas/{id}        : consulta el detalle de una factura.
 *  - POST /api/facturas             : crea (factura) una nueva venta.
 *  - POST /api/facturas/{id}/anular : anula una factura existente.
 */
@RestController
@RequestMapping("/api/facturas")
public class FacturaRestController {

    private final FacturaService facturaService;

    public FacturaRestController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /** Lista todas las facturas, de la mas reciente a la mas antigua. */
    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    /** Consulta el detalle de una factura por su id. */
    @GetMapping("/{id}")
    public ResponseEntity<?> consultar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(facturaService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje(ex.getMessage()));
        }
    }

    /**
     * Registra una nueva venta (factura).
     *
     * Cuerpo esperado (JSON):
     * { "nombreCliente": "...", "documentoCliente": "...",
     *   "items": [ { "productoId": "...", "cantidad": 2 } ] }
     *
     * El "vendedor" se fija como "api" porque esta ruta no requiere sesion
     * de navegador (se prueba directamente desde Postman); si mas adelante
     * la API se protege con un token por usuario, aqui se tomaria el
     * nombre del usuario autenticado igual que en FacturaController.
     */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody FacturaDTO datos, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Datos invalidos");
            respuesta.put("errores", result.getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .toList());
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {
            Factura factura = facturaService.crear(datos, "api");
            return ResponseEntity.status(HttpStatus.CREATED).body(factura);
        } catch (IllegalArgumentException ex) {
            // Producto inexistente o stock insuficiente
            return ResponseEntity.badRequest().body(mensaje(ex.getMessage()));
        }
    }

    /** Anula una factura existente y reintegra el stock de sus items. */
    @PostMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable String id) {
        try {
            facturaService.anular(id);
            return ResponseEntity.ok(mensaje("Factura anulada exitosamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje(ex.getMessage()));
        }
    }

    private Map<String, String> mensaje(String texto) {
        Map<String, String> m = new HashMap<>();
        m.put("mensaje", texto);
        return m;
    }
}
