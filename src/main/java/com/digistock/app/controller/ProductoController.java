package com.digistock.app.controller;

import com.digistock.app.dto.ProductoDTO;
import com.digistock.app.model.Producto;
import com.digistock.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador web (Thymeleaf) del modulo de inventario.
 *
 * Permite ver, crear, editar y eliminar productos desde el navegador.
 * Reutiliza ProductoService (la misma logica que usa la API REST en
 * /api/productos), de modo que ambas vias aplican las mismas reglas.
 *
 * El acceso a estas rutas se controla en SecurityConfig: cualquier usuario
 * autenticado puede consultar el listado (el VENDEDOR necesita ver el
 * inventario), pero solo ADMINISTRADOR y ALMACENISTA pueden crear, editar
 * o eliminar productos.
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** Lista todos los productos del inventario. */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos";
    }

    /** Muestra el formulario para registrar un producto nuevo. */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("esNuevo", true);
        return "producto-form";
    }

    /** Guarda un producto nuevo enviado desde el formulario. */
    @PostMapping
    public String crear(@Valid @ModelAttribute("producto") ProductoDTO datos,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("esNuevo", true);
            return "producto-form";
        }
        productoService.crear(datos);
        return "redirect:/productos";
    }

    /** Muestra el formulario para editar un producto existente. */
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable String id, Model model) {
        Producto existente = productoService.buscarPorId(id);
        ProductoDTO datos = new ProductoDTO();
        datos.setNombre(existente.getNombre());
        datos.setPrecio(existente.getPrecio());
        datos.setStockDisponible(existente.getStockDisponible());
        model.addAttribute("producto", datos);
        model.addAttribute("productoId", id);
        model.addAttribute("esNuevo", false);
        return "producto-form";
    }

    /** Guarda los cambios de un producto existente. */
    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("producto") ProductoDTO datos,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productoId", id);
            model.addAttribute("esNuevo", false);
            return "producto-form";
        }
        productoService.actualizar(id, datos);
        return "redirect:/productos";
    }

    /** Elimina un producto del inventario. */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}
