package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.Factura;
import com.tienda.tiendavirtual.model.Usuario;
import com.tienda.tiendavirtual.service.FacturaService;
import com.tienda.tiendavirtual.service.PdfService;
import com.tienda.tiendavirtual.service.PedidoService;
import com.tienda.tiendavirtual.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FacturaController {

    @Autowired private FacturaService facturaService;
    @Autowired private PdfService pdfService;
    @Autowired private PedidoService pedidoService;
    @Autowired private UsuarioService usuarioService;

    // Ver factura en pantalla
    @GetMapping("/factura/{id}")
    public String verFactura(@PathVariable Long id, Model model) {
        Factura factura = facturaService.findById(id);
        model.addAttribute("factura", factura);
        return "factura/detalle";
    }

    // Descargar la factura como PDF
    @GetMapping("/factura/descargar/{id}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) throws Exception {
        Factura factura = facturaService.findById(id);
        byte[] pdf = pdfService.generarPdf(factura);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline",
                "factura-" + factura.getNumero() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    // Historial de pedidos del usuario logueado
    @GetMapping("/mis-pedidos")
    public String misPedidos(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("pedidos", pedidoService.listarPorUsuario(usuario.getId()));
        return "factura/mis-pedidos";
    }
    // Redirige desde el ID del pedido a la factura correspondiente
    @GetMapping("/factura/por-pedido/{pedidoId}")
    public String verFacturaPorPedido(@PathVariable Long pedidoId) {
        Factura factura = facturaService.findByPedido(pedidoId);
        return "redirect:/factura/" + factura.getId();
    }

}