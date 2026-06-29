// CategoriaRepository.java
package com.tienda.tiendavirtual.repository;
import com.tienda.tiendavirtual.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}