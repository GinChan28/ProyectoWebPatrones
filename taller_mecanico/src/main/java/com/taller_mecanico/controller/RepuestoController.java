/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Repuesto;
import com.taller_mecanico.repository.RepuestoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author sebas
 */
@Controller
@RequestMapping("/repuesto")
public class RepuestoController {

    private final RepuestoRepository repuestoRepo;

    public RepuestoController(RepuestoRepository repuestoRepo) {
        this.repuestoRepo = repuestoRepo;
    }

    // Mostrar formulario para crear nuevo repuesto
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("repuesto", new Repuesto());
        return "repuesto/formulario";
    }

    // Guardar nuevo repuesto
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Repuesto repuesto) {
        repuesto.setActivo(true); // Por defecto activo
        repuestoRepo.save(repuesto);
        return "redirect:/inventario/repuestos";
    }

    // Mostrar formulario para editar repuesto existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model) {
        Repuesto repuesto = repuestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));
        model.addAttribute("repuesto", repuesto);
        return "repuesto/formulario";
    }

    // Eliminar repuesto (soft delete: marcar como inactivo)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id) {
        Repuesto repuesto = repuestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));
        repuesto.setActivo(false);
        repuestoRepo.save(repuesto);
        return "redirect:/inventario/repuestos";
    }
}