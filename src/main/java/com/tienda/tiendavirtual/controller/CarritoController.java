package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.ItemCarrito;
import com.tienda.tiendavirtual.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // Ver el carrito
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<ItemCarrito> items = carritoService.obtenerCarrito(session);
        Double total = carritoService.calcularTotal(session);

        // Calcular IGV y total con impuesto
        Double igv = total * 0.18;
        Double totalConIgv = total + igv;

        model.addAttribute("items", items);
        model.addAttribute("subtotal", total);
        model.addAttribute("igv", igv);
        model.addAttribute("totalConIgv", totalConIgv);
        model.addAttribute("cantidadItems",
                carritoService.contarItems(session));

        return "carrito/index";
    }

    // Agregar producto al carrito desde el catálogo
    @PostMapping("/agregar")
    public String agregar(@RequestParam Long productoId,
                          @RequestParam(defaultValue = "1") Integer cantidad,
                          HttpSession session,
                          RedirectAttributes flash) {
        try {
            carritoService.agregar(productoId, cantidad, session);
            flash.addFlashAttribute("exito",
                    "Producto agregado al carrito");
        } catch (Exception e) {
            flash.addFlashAttribute("error",
                    "No se pudo agregar el producto");
        }
        // Regresa al catálogo
        return "redirect:/";
    }

    // Aumentar cantidad (+1)
    @PostMapping("/aumentar/{productoId}")
    public String aumentar(@PathVariable Long productoId,
                           HttpSession session) {
        carritoService.aumentar(productoId, session);
        return "redirect:/carrito";
    }

    // Reducir cantidad (-1)
    @PostMapping("/reducir/{productoId}")
    public String reducir(@PathVariable Long productoId,
                          HttpSession session) {
        carritoService.reducir(productoId, session);
        return "redirect:/carrito";
    }

    // Eliminar un producto del carrito
    @PostMapping("/eliminar/{productoId}")
    public String eliminar(@PathVariable Long productoId,
                           HttpSession session,
                           RedirectAttributes flash) {
        carritoService.eliminar(productoId, session);
        flash.addFlashAttribute("exito", "Producto eliminado del carrito");
        return "redirect:/carrito";
    }

    // Vaciar todo el carrito
    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {
        carritoService.vaciar(session);
        return "redirect:/carrito";
    }
}