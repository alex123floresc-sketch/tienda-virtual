package com.tienda.tiendavirtual.service;

import com.tienda.tiendavirtual.model.*;
import com.tienda.tiendavirtual.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    private static final Double TASA_IGV = 0.18;

    // Crea el pedido a partir del carrito en sesión
    public Pedido crearPedido(HttpSession session, Usuario usuario, String tipoPago) {
        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Verifica stock antes de confirmar (evita vender lo que no hay)
        for (ItemCarrito item : items) {
            Producto producto = productoService.findById(item.getProductoId());
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException(
                        "Stock insuficiente para: " + producto.getNombre());
            }
        }

        Double subtotal = carritoService.calcularTotal(session);
        Double igv = calcularIgv(subtotal);
        Double total = subtotal + igv;

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTipoPago(tipoPago);
        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");

        // Crea el detalle por cada item del carrito
        for (ItemCarrito item : items) {
            Producto producto = productoService.findById(item.getProductoId());

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());

            pedido.getDetalles().add(detalle);

            // Descuenta el stock vendido
            producto.setStock(producto.getStock() - item.getCantidad());
            productoService.actualizarStock(producto);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Vacía el carrito una vez confirmado el pedido
        carritoService.vaciar(session);

        return pedidoGuardado;
    }

    public Double calcularIgv(Double subtotal) {
        return subtotal * TASA_IGV;
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            return pedidoRepository.findAll();
        }
        return pedidoRepository.findByEstado(estado);
    }

    public void actualizarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = findById(pedidoId);
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }
}