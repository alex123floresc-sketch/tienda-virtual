package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.Categoria;
import com.tienda.tiendavirtual.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin/categorias") // CAMBIADO@PreAuthorize("hasRole('ADMIN')") //
public class AdminCategoriaController {

    @Autowired private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/categorias/lista";
    }

    // Recibe tanto creación como edición desde los modales del HTML
    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Long id,
                          @RequestParam String nombre,
                          @RequestParam(required = false) String descripcion,
                          @RequestParam(defaultValue = "true") Boolean activo,
                          RedirectAttributes flash) {
        Categoria categoria = new Categoria();
        categoria.setId(id); // null si es nueva, con valor si es edición
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setActivo(activo);

        categoriaService.guardar(categoria);
        flash.addFlashAttribute("exito", "Categoría guardada correctamente");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        categoriaService.eliminar(id);
        flash.addFlashAttribute("exito", "Categoría eliminada");
        return "redirect:/admin/categorias";
    }
}