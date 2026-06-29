package com.tienda.tiendavirtual.service;

import com.tienda.tiendavirtual.model.Producto;
import com.tienda.tiendavirtual.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Carpeta donde se guardan las imágenes
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> buscar(String nombre) {
        return productoRepository
                .findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void guardar(Producto producto, MultipartFile imagen)
            throws IOException {
        // Solo procesa la imagen si el usuario subió una
        if (imagen != null && !imagen.isEmpty()) {
            // Genera nombre único para evitar colisiones
            String nombreArchivo = UUID.randomUUID() + "_"
                    + imagen.getOriginalFilename();
            Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.copy(imagen.getInputStream(), ruta,
                    StandardCopyOption.REPLACE_EXISTING);
            producto.setImagen(nombreArchivo);
        }
        productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto p = findById(id);
        p.setActivo(false); // borrado lógico, no físico
        productoRepository.save(p);
    }

    public void actualizarStock(Producto producto) {

    }
}