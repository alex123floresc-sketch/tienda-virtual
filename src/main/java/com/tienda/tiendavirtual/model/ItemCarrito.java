package com.tienda.tiendavirtual.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrito implements Serializable {

    private Long productoId;
    private String nombre;
    private Double precioUnitario;
    private Integer cantidad;
    private String imagen;

    public Double getSubtotal() {
        return precioUnitario * cantidad;
    }
}