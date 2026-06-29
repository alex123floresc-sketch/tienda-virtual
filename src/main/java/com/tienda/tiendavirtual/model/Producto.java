package com.tienda.tiendavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    private String imagen; // nombre del archivo guardado

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con Categoria: muchos productos -> una categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist // se ejecuta automáticamente antes de guardar
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}