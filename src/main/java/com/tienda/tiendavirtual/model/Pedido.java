package com.tienda.tiendavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data // Lombok genera automáticamente getters y setters
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private String estado = "PENDIENTE";

    private Double subtotal;
    private Double igv;
    private Double total;

    @Column(name = "tipo_pago")
    private String tipoPago;

    // Campos faltantes que causaban el error "cannot find symbol"
    private String nombreCliente;
    private String emailCliente;
    private String direccionCliente;
    private String telefonoCliente;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}