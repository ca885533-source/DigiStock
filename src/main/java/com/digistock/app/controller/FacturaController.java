package com.digistock.app.controller;

import com.digistock.app.dto.FacturaDTO;
import com.digistock.app.repository.ProductoRepository;
import com.digistock.app.security.UsuarioPrincipal;
import com.digistock.app.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador del modulo de facturacion: listar ventas, crear una nueva
 * factura, ver el detalle de una factura y anularla.
 *
 * El acceso a "/facturas/**" esta restringido en SecurityConfig a los
 * roles ADMINISTRADOR y VENDEDOR (el ALMACENISTA no factura).
 *
 * Historias de usuario cubiertas:
 *  - HU: "Como vendedor quiero registrar una venta seleccionando productos
 *         y cantidades, para generar una factura con el total a cobrar".
 *  - HU: "Como vendedor quiero consultar el historial de facturas
 *         generadas, para hacer seguimiento a mis ventas".
 *  - HU: "Como administrador quiero poder anular una factura erronea,
 *         para que el inventario y los reportes queden correctos".
 */
@Controller
public class FacturaController {

    private final FacturaService facturaService;
    private final ProductoRepository productoRepository;

    public FacturaController(FacturaService facturaService, ProductoRepository productoRepository) {
        this.facturaService = facturaService;
        this.productoRepository = productoRepository;
    }

    /** Lista todas las facturas registradas, de la mas reciente a la mas antigua. */
    @GetMapping("/facturas")
    public String listar(Model model) {
        model.addAttribute("facturas", facturaService.listarTodas());
        return "facturas";
    }

    /** Muestra el formulario para registrar una nueva venta. */
    @GetMapping("/facturas/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("facturaDTO", new FacturaDTO());
        model.addAttribute("productos", productoRepository.findAll());
        return "factura-form";
    }

    /**
     * Procesa el formulario de creacion de factura.
     *
     * @Valid activa las validaciones del DTO (cliente obligatorio, al menos
     * un item). Los errores de negocio (stock insuficiente, producto
     * inexistente) se capturan aparte porque dependen de datos que solo
     * se conocen al consultar la base de datos, no de anotaciones estaticas.
     */
    @PostMapping("/facturas")
    public String crear(@Valid @ModelAttribute FacturaDTO facturaDTO,
                         BindingResult result,
                         @AuthenticationPrincipal UsuarioPrincipal principal,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoRepository.findAll());
            return "factura-form";
        }

        try {
            var factura = facturaService.crear(facturaDTO, principal.getUsername());
            return "redirect:/facturas/" + factura.getId();
        } catch (IllegalArgumentException ex) {
            // Errores de negocio: producto inexistente o stock insuficiente
            model.addAttribute("errorNegocio", ex.getMessage());
            model.addAttribute("productos", productoRepository.findAll());
            return "factura-form";
        }
    }

    /** Muestra el detalle de una factura ya generada (vista tipo comprobante). */
    @GetMapping("/facturas/{id}")
    public String verDetalle(@PathVariable String id, Model model) {
        model.addAttribute("factura", facturaService.buscarPorId(id));
        return "factura-detalle";
    }

    /** Anula una factura y reintegra el stock de sus items al inventario. */
    @PostMapping("/facturas/{id}/anular")
    public String anular(@PathVariable String id) {
        facturaService.anular(id);
        return "redirect:/facturas/" + id;
    }
}
