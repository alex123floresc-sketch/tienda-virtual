package com.tienda.tiendavirtual.controller;

import com.tienda.tiendavirtual.model.Usuario;
import com.tienda.tiendavirtual.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Muestra el formulario de login
    // Devuelve "login/login" porque tu carpeta se llama login/
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    // Muestra el formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login/registro";
    }

    // Procesa el registro enviado desde el formulario
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioService.existeEmail(usuario.getEmail())) {
            model.addAttribute("error", "Ese correo ya está registrado");
            model.addAttribute("usuario", usuario);
            return "login/registro";
        }

        usuarioService.registrar(usuario);
        return "redirect:/login?registroExitoso=true";
    }
}