package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.*;
import com.tienda.tiendavirtual.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private CarritoService carritoService;
    @Autowired private PedidoService pedidoService;
    @Autowired private FacturaService facturaService;
    @Autowired private UsuarioService usuarioService;

    // Muestra el formulario de checkout con el resumen del carrito
    @GetMapping
    public String mostrarCheckout(HttpSession session, Model model,
                                  Authentication authentication) {
        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        // Trae el usuario logueado completo desde la BD
        Usuario usuario = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Double subtotal = carritoService.calcularTotal(session);
        Double igv = pedidoService.calcularIgv(subtotal);
        Double total = subtotal + igv;

        model.addAttribute("usuario", usuario);
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("igv", igv);
        model.addAttribute("total", total);

        return "checkout/formulario";
    }

    // Procesa el formulario y crea pedido + factura
    @PostMapping("/confirmar")
    public String confirmar(@RequestParam String tipoPago,
                            @RequestParam String direccion,
                            @RequestParam String telefono,
                            HttpSession session,
                            Model model,
                            Authentication authentication) {
        try {
            Usuario usuario = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualiza datos de contacto antes de procesar
            usuario.setDireccion(direccion);
            usuario.setTelefono(telefono);

            Pedido pedido = pedidoService.crearPedido(session, usuario, tipoPago);
            Factura factura = facturaService.generarFactura(pedido);

            model.addAttribute("pedido", pedido);
            model.addAttribute("factura", factura);
            return "checkout/exito";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }
}