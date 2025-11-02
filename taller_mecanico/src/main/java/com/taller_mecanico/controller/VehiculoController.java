/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author megan
 */
@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    private final VehiculoRepository vehiculoRepo;
    private final ClienteRepository clienteRepo;

    public VehiculoController(VehiculoRepository v, ClienteRepository c) {
        this.vehiculoRepo = v;
        this.clienteRepo = c;
    }

    // DEMO: usa el primer cliente de la BD mientras no integramos login
    private Integer idClienteDemo() {
        return clienteRepo.findAll().stream()
                .findFirst()
                .map(Cliente::getIdCliente)
                .orElseThrow(() -> new IllegalStateException("No hay clientes en BD"));
    }

    @GetMapping("/listado")
    public String listadoVehiculos(Model model) {
        model.addAttribute("vehiculos", vehiculoRepo.findByClienteIdCliente(idClienteDemo()));
        return "/vehiculo/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "/vehiculo/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Vehiculo vehiculo) {
        var cli = clienteRepo.findById(idClienteDemo()).orElseThrow();
        vehiculo.setCliente(cli);
        vehiculoRepo.save(vehiculo);
        return "redirect:/vehiculo/listado";
    }
}
