// ProductoRepository.java
package com.tienda.tiendavirtual.repository;
import com.tienda.tiendavirtual.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Spring genera el SQL automáticamente por el nombre del método:
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByActivoTrue();
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);



}