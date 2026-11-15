package com.digistock.app.controller.api;

import com.digistock.app.dto.FacturaDTO;
import com.digistock.app.model.Factura;
import com.digistock.app.service.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicio REST de facturacion. Reutiliza FacturaService, por lo que las
 * reglas de negocio (stock, IVA 19%, precio leido de la base de datos,
 * reintegro de stock al anular) son las mismas que usa la aplicacion web.
 *
 * Crear y consultar: ADMINISTRADOR o VENDEDOR. Anular: solo ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Registro, consulta y anulacion de facturas de venta")
@SecurityRequirement(name = "basicAuth")
public class FacturaApiController {

    private final FacturaService facturaService;

    public FacturaApiController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @Operation(summary = "Crear una factura (descuenta stock y calcula IVA)")
    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody FacturaDTO datos,
                                         Authentication autenticacion) {
        Factura factura = facturaService.crear(datos, autenticacion.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }

    @Operation(summary = "Listar facturas, de la mas reciente a la mas antigua")
    @GetMapping
    public List<Factura> listar() {
        return facturaService.listarTodas();
    }

    @Operation(summary = "Consultar una factura por su id")
    @GetMapping("/{id}")
    public Factura buscar(@PathVariable String id) {
        return facturaService.buscarPorId(id);
    }

    @Operation(summary = "Anular una factura (reintegra el stock)")
    @PatchMapping("/{id}/anular")
    public Factura anular(@PathVariable String id) {
        facturaService.anular(id);
        return facturaService.buscarPorId(id);
    }
}
