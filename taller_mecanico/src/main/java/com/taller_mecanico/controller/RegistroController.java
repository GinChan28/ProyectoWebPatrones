/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Rol;
import com.taller_mecanico.domain.Usuario;
import com.taller_mecanico.domain.UsuarioRol;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.RolRepository;
import com.taller_mecanico.repository.UsuarioRepository;
import com.taller_mecanico.repository.UsuarioRolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author megan
 */
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final UsuarioRolRepository usuarioRolRepo;
    private final ClienteRepository clienteRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistroController(UsuarioRepository u, RolRepository r, UsuarioRolRepository ur,
            ClienteRepository c, PasswordEncoder p) {
        this.usuarioRepo = u;
        this.rolRepo = r;
        this.usuarioRolRepo = ur;
        this.clienteRepo = c;
        this.passwordEncoder = p;
    }

    @GetMapping
    public String formRoot(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro/nuevo";
    }

    @GetMapping("/nuevo")
    public String form(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono) {

        //Validamos si esta duplicado 
        if (usuarioRepo.findByUsername(username).isPresent()) {
            return "redirect:/registro/nuevo?existe";
        }

        // Guarda usuario 
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setActivo(true);
        u = usuarioRepo.save(u);

        // Asigna rol al CLIENTE
        Rol rolCliente = rolRepo.findByRol("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Debe existir el rol CLIENTE"));

        UsuarioRol ur = new UsuarioRol();
        ur.setIdUsuario(u.getIdUsuario());
        ur.setIdRol(rolCliente.getIdRol());

        ur.setUsuario(u);
        ur.setRol(rolCliente);

        usuarioRolRepo.save(ur);

        // Crea perfil Cliente
        Cliente cliente = new Cliente();
        cliente.setUsuario(u);
        cliente.setNombre(nombre + " " + apellidos);
        cliente.setTelefono(telefono);
        clienteRepo.save(cliente);

        return "redirect:/login?registrado";
    }
}
