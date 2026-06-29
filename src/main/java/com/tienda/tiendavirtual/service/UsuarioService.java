package com.tienda.tiendavirtual.service;

import com.tienda.tiendavirtual.model.Usuario;
import com.tienda.tiendavirtual.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // viene del Bean en SecurityConfig

    public void registrar(Usuario usuario) {
        // Encripta la contraseña antes de guardarla — nunca texto plano
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("CLIENTE"); // todo registro público nace como cliente
        usuarioRepository.save(usuario);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}