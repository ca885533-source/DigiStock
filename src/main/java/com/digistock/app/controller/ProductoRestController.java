package com.digistock.app.controller;

import com.digistock.app.model.Producto;
import com.digistock.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST del modulo de inventario (productos) del sistema DigiStock.
 *
 * Reutiliza ProductoService (la misma logica de negocio que usa el
 * modulo web ProductoController), exponiendola como servicios JSON para
 * que el inventario pueda gestionarse desde Postman o desde cualquier
 * cliente externo.
 *
 * Servicios expuestos:
 *  - GET    /api/productos       : lista todos los productos.
 *  - GET    /api/productos/{id}  : consulta un producto por su id.
 *  - POST   /api/productos       : crea un nuevo producto.
 *  - PUT    /api/productos/{id}  : actualiza un producto existente.
 *  - DELETE /api/productos/{id}  : elimina un producto.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoService productoService;

    public ProductoRestController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** Lista todos los productos del inventario. */
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    /** Consulta un producto especifico por su id. Responde 404 si no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<?> consultar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productoService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje(ex.getMessage()));
        }
    }

    /**
     * Crea un nuevo producto en el inventario.
     * Cuerpo esperado (JSON): { "nombre": "...", "precio": 0.0, "stockDisponible": 0 }
     */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Producto producto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresDeValidacion(result));
        }
        Producto creado = productoService.crear(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Actualiza el nombre, precio y/o stock de un producto existente. */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody Producto datos, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresDeValidacion(result));
        }
        try {
            return ResponseEntity.ok(productoService.actualizar(id, datos));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje(ex.getMessage()));
        }
    }

    /** Elimina un producto del inventario. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.ok(mensaje("Producto eliminado exitosamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje(ex.getMessage()));
        }
    }

    private Map<String, String> mensaje(String texto) {
        Map<String, String> m = new HashMap<>();
        m.put("mensaje", texto);
        return m;
    }

    private Map<String, Object> erroresDeValidacion(BindingResult result) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Datos invalidos");
        respuesta.put("errores", result.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList());
        return respuesta;
    }
}
