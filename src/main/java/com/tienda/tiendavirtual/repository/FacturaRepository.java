package com.tienda.tiendavirtual.repository;

import com.tienda.tiendavirtual.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findTopByOrderByIdDesc(); // última factura, para el correlativo
    Optional<Factura> findByPedidoId(Long pedidoId);
    List<Factura> findByNumeroContainingIgnoreCase(String numero);
}