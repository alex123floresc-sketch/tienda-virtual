package com.tienda.tiendavirtual.service;

import com.tienda.tiendavirtual.model.Factura;
import com.tienda.tiendavirtual.model.Pedido;
import com.tienda.tiendavirtual.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    // Genera y guarda la factura asociada a un pedido ya creado
    public Factura generarFactura(Pedido pedido) {
        Factura factura = new Factura();
        factura.setNumero(generarNumero());
        factura.setPedido(pedido);
        factura.setSubtotal(pedido.getSubtotal());
        factura.setIgv(pedido.getIgv());
        factura.setTotal(pedido.getTotal());
        factura.setEstado("EMITIDA");

        return facturaRepository.save(factura);
    }

    // Genera un número correlativo: FAC-2026-0001, FAC-2026-0002...
    public String generarNumero() {
        int anio = Year.now().getValue();
        Optional<Factura> ultima = facturaRepository.findTopByOrderByIdDesc();

        int siguienteNumero = 1;
        if (ultima.isPresent()) {
            // Extrae el número del último correlativo y le suma 1
            String numeroAnterior = ultima.get().getNumero();
            String[] partes = numeroAnterior.split("-");
            if (partes.length == 3 && partes[1].equals(String.valueOf(anio))) {
                siguienteNumero = Integer.parseInt(partes[2]) + 1;
            }
        }

        return String.format("FAC-%d-%04d", anio, siguienteNumero);
    }

    public Factura findById(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    public Factura findByPedido(Long pedidoId) {
        return facturaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada para este pedido"));
    }

    public List<Factura> listarTodas() {
        return facturaRepository.findAll();
    }

    public List<Factura> buscarPorNumero(String numero) {
        if (numero == null || numero.isEmpty()) {
            return facturaRepository.findAll();
        }
        return facturaRepository.findByNumeroContainingIgnoreCase(numero);
    }

    public Double calcularTotalFacturado(List<Factura> facturas) {
        return facturas.stream()
                .mapToDouble(Factura::getTotal)
                .sum();
    }
}