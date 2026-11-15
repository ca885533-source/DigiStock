package com.digistock.app.controller;

import com.digistock.app.model.Factura;
import com.digistock.app.service.FacturaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Pagina publica de verificacion de facturas (no requiere iniciar sesion).
 *
 * Es la direccion que lleva el codigo QR impreso en la factura. Solo muestra
 * los datos de la compra para confirmar la autenticidad (numero, fecha,
 * nombre del cliente, productos, totales, estado y CUFE); no expone el
 * documento del cliente ni el vendedor.
 */
@Controller
public class VerificacionController {

    private final FacturaService facturaService;

    public VerificacionController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/verificar/{cufe}")
    public String verificar(@PathVariable String cufe, Model model) {
        Optional<Factura> factura = facturaService.buscarPorCufe(cufe);
        model.addAttribute("valida", factura.isPresent());
        model.addAttribute("factura", factura.orElse(null));
        model.addAttribute("cufe", cufe);
        return "verificar-factura";
    }
}
