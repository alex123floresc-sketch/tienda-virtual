package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.service.CategoriaService;
import com.tienda.tiendavirtual.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CatalogoController {

    @Autowired private ProductoService productoService;
    @Autowired private CategoriaService categoriaService;

    // Página principal — catálogo
    @GetMapping("/")
    public String catalogo(@RequestParam(required = false) String buscar,
                           @RequestParam(required = false) Long categoria,
                           Model model) {
        // Decide qué mostrar según los filtros
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("productos", productoService.buscar(buscar));
            model.addAttribute("busqueda", buscar);
        } else if (categoria != null) {
            model.addAttribute("productos",
                    productoService.listarPorCategoria(categoria));
            model.addAttribute("categoriaSeleccionada", categoria);
        } else {
            model.addAttribute("productos", productoService.listarTodos());
        }
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "catalogo/index";
    }

    // Detalle de un producto
    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.findById(id));
        return "catalogo/detalle";
    }
}