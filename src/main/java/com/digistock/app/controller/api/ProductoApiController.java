package com.digistock.app.controller.api;

import com.digistock.app.dto.AjusteStockDTO;
import com.digistock.app.dto.ProductoDTO;
import com.digistock.app.model.Producto;
import com.digistock.app.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicio REST de inventario de productos.
 *
 * Consulta: cualquier usuario autenticado.
 * Crear, actualizar, ajustar stock y eliminar: ADMINISTRADOR o ALMACENISTA.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Inventario: CRUD de productos y ajuste de stock")
@SecurityRequirement(name = "basicAuth")
public class ProductoApiController {

    private final ProductoService productoService;

    public ProductoApiController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar productos")
    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @Operation(summary = "Consultar un producto por su id")
    @GetMapping("/{id}")
    public Producto buscar(@PathVariable String id) {
        return productoService.buscarPorId(id);
    }

    @Operation(summary = "Crear un producto")
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(datos));
    }

    @Operation(summary = "Actualizar nombre, precio y stock de un producto")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable String id, @Valid @RequestBody ProductoDTO datos) {
        return productoService.actualizar(id, datos);
    }

    @Operation(summary = "Registrar entrada (+) o salida (-) de stock")
    @PatchMapping("/{id}/stock")
    public Producto ajustarStock(@PathVariable String id, @Valid @RequestBody AjusteStockDTO datos) {
        return productoService.ajustarStock(id, datos.getCantidad());
    }

    @Operation(summary = "Eliminar un producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
