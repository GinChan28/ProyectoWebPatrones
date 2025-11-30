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

@Controller
@RequestMapping("/repuesto")
public class RepuestoController {

    private final RepuestoRepository repuestoRepo;

    public RepuestoController(RepuestoRepository repuestoRepo) {
        this.repuestoRepo = repuestoRepo;
    }

    
    @GetMapping("/formulario")
    public String verListado(Model model) {
        model.addAttribute("repuestos", repuestoRepo.findAll());
        return "repuesto/formulario";
    }

   
    @GetMapping("/acciones")
    public String verAcciones(Model model) {
        model.addAttribute("repuestos", repuestoRepo.findAll());
        return "repuesto/acciones";
    }

   
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("repuesto", new Repuesto());
        return "repuesto/formulario";
    }

    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Repuesto repuesto) {

        if (repuesto.getIdRepuesto() == null) {
            repuesto.setActivo(true);
        }

        repuestoRepo.save(repuesto);
        return "redirect:/repuesto/acciones";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model) {
        Repuesto repuesto = repuestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));

        model.addAttribute("repuesto", repuesto);
        return "repuesto/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id) {
        Repuesto repuesto = repuestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado: " + id));

        repuesto.setActivo(false);
        repuestoRepo.save(repuesto);

        return "redirect:/repuesto/acciones";
    }
}
