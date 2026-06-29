package com.tienda.tiendavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero; // FAC-2024-0001

    private LocalDateTime fecha;

    private Double subtotal;
    private Double igv;
    private Double total;

    // Una factura pertenece a un pedido (relación 1 a 1)
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    public void setEstado(String emitida) {
    }
}