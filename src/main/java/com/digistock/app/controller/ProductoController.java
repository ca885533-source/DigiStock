package com.digistock.app.controller;

import com.digistock.app.model.Producto;
import com.digistock.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador web (Thymeleaf) del modulo de inventario.
 *
 * Permite ver, crear, editar y eliminar productos desde el navegador.
 * El acceso a estas rutas esta restringido a los roles ADMINISTRADOR y
 * ALMACENISTA desde SecurityConfig; el VENDEDOR solo puede consultar el
 * listado (ver GET /productos), ya que su rol le permite "consultar
 * inventario" pero no modificarlo.
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
        model.addAttribute("productos", productoService.listarTodos());
        return "productos";
    }

    /** Muestra el formulario para registrar un producto nuevo. */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("esNuevo", true);
        return "producto-form";
    }

    /** Guarda un producto nuevo enviado desde el formulario. */
    @PostMapping
    public String crear(@Valid @ModelAttribute("producto") Producto producto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("esNuevo", true);
            return "producto-form";
        }
        productoService.crear(producto);
        return "redirect:/productos";
    }

    /** Muestra el formulario para editar un producto existente. */
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable String id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("esNuevo", false);
        return "producto-form";
    }

    /** Guarda los cambios de un producto existente. */
    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id, @Valid @ModelAttribute("producto") Producto datos,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
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
