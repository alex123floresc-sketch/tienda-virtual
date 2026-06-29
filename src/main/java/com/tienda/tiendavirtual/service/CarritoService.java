package com.tienda.tiendavirtual.service;

import com.tienda.tiendavirtual.model.ItemCarrito;
import com.tienda.tiendavirtual.model.Producto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    // Clave con la que guardamos el carrito en la sesión
    private static final String CLAVE_CARRITO = "carrito";

    @Autowired
    private ProductoService productoService;

    // Obtiene el carrito de la sesión (o crea uno vacío)
    public List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito =
                (List<ItemCarrito>) session.getAttribute(CLAVE_CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(CLAVE_CARRITO, carrito);
        }
        return carrito;
    }

    // Agrega un producto al carrito
    // Si ya existe, solo aumenta la cantidad
    public void agregar(Long productoId, Integer cantidad, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        Producto producto = productoService.findById(productoId);

        // Busca si el producto ya está en el carrito
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            // Si ya existe, suma la cantidad
            ItemCarrito item = existente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            // Verifica que no supere el stock
            if (nuevaCantidad <= producto.getStock()) {
                item.setCantidad(nuevaCantidad);
            }
        } else {
            // Si no existe, crea un item nuevo
            ItemCarrito nuevoItem = new ItemCarrito(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getImagen()
            );
            carrito.add(nuevoItem);
        }

        session.setAttribute(CLAVE_CARRITO, carrito);
    }

    // Aumenta la cantidad de un producto en 1
    public void aumentar(Long productoId, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        Producto producto = productoService.findById(productoId);

        carrito.stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(item -> {
                    if (item.getCantidad() < producto.getStock()) {
                        item.setCantidad(item.getCantidad() + 1);
                    }
                });

        session.setAttribute(CLAVE_CARRITO, carrito);
    }

    // Reduce la cantidad en 1 (si llega a 0, elimina el item)
    public void reducir(Long productoId, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);

        carrito.stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(item -> {
                    if (item.getCantidad() > 1) {
                        item.setCantidad(item.getCantidad() - 1);
                    } else {
                        carrito.remove(item);
                    }
                });

        session.setAttribute(CLAVE_CARRITO, carrito);
    }

    // Elimina un producto del carrito completamente
    public void eliminar(Long productoId, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProductoId().equals(productoId));
        session.setAttribute(CLAVE_CARRITO, carrito);
    }

    // Vacía todo el carrito
    public void vaciar(HttpSession session) {
        session.removeAttribute(CLAVE_CARRITO);
    }

    // Calcula el total de todos los items
    public Double calcularTotal(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    // Cuenta cuántos items hay en el carrito (para el badge del navbar)
    public int contarItems(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
}