// DetallePedidoRepository.java
package com.tienda.tiendavirtual.repository;
import com.tienda.tiendavirtual.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}