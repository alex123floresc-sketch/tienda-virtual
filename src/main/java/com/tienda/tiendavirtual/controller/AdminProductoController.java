package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.Producto;
import com.tienda.tiendavirtual.service.CategoriaService;
import com.tienda.tiendavirtual.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoController {

    @Autowired private ProductoService productoService;
    @Autowired private CategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("productos", productoService.buscar(buscar));
            model.addAttribute("busqueda", buscar);
        } else {
            model.addAttribute("productos", productoService.listarTodos());
        }
        return "admin/productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.findById(id));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/productos/formulario";
    }

    // RUTA MODIFICADA AQUÍ
    @PostMapping("/guardar-producto")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("archivoImagen") MultipartFile imagen,
                          RedirectAttributes flash) {
        try {
            productoService.guardar(producto, imagen);
            flash.addFlashAttribute("exito", "Producto guardado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        productoService.eliminar(id);
        flash.addFlashAttribute("exito", "Producto eliminado");
        return "redirect:/admin/productos";
    }
}