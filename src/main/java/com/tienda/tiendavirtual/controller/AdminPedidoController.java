package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.Factura;
import com.tienda.tiendavirtual.model.Pedido;
import com.tienda.tiendavirtual.service.FacturaService;
import com.tienda.tiendavirtual.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin/pedidos") // CAMBIADO@PreAuthorize("hasRole('ADMIN')") //
public class AdminPedidoController {

    @Autowired private PedidoService pedidoService;
    @Autowired private FacturaService facturaService;

    // Lista de pedidos con filtro opcional por estado
    @GetMapping("/pedidos")
    public String listarPedidos(@RequestParam(required = false) String estado, Model model) {
        model.addAttribute("pedidos", pedidoService.listarPorEstado(estado));
        model.addAttribute("estadoFiltro", estado);
        return "admin/pedidos/lista";
    }

    // Detalle de un pedido + su factura asociada
    @GetMapping("/pedidos/{id}")
    public String detallePedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.findById(id);
        model.addAttribute("pedido", pedido);

        // La factura puede no existir si algo falló al crearla
        try {
            Factura factura = facturaService.findByPedido(id);
            model.addAttribute("factura", factura);
        } catch (RuntimeException e) {
            model.addAttribute("factura", null);
        }

        return "admin/pedidos/detalle";
    }

    // Cambia el estado de un pedido
    @PostMapping("/pedidos/{id}/estado")
    public String actualizarEstado(@PathVariable Long id,
                                   @RequestParam String estado,
                                   RedirectAttributes flash) {
        pedidoService.actualizarEstado(id, estado);
        flash.addFlashAttribute("exito", "Estado actualizado a " + estado);
        return "redirect:/admin/pedidos/" + id;
    }

    // Lista de todas las facturas emitidas
    @GetMapping("/facturas")
    public String listarFacturas(@RequestParam(required = false) String buscar, Model model) {
        List<Factura> facturas = facturaService.buscarPorNumero(buscar);
        model.addAttribute("facturas", facturas);
        model.addAttribute("busqueda", buscar);
        model.addAttribute("totalFacturado", facturaService.calcularTotalFacturado(facturas));
        return "admin/facturas/lista";
    }
}