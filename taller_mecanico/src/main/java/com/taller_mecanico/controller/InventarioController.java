/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import com.taller_mecanico.repository.RepuestoRepository;
import com.taller_mecanico.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author megan
 */
@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final RepuestoRepository repuestoRepository;
    private final InventarioService inventarioService;

    public InventarioController(RepuestoRepository r, InventarioService i) {
        this.repuestoRepository = r;
        this.inventarioService = i;
    }

    @GetMapping("/repuestos")
    public String repuestos(Model model) {
        model.addAttribute("repuestos", repuestoRepository.findAll());
        return "/inventario/repuestos";
    }

    @PostMapping("/consumir")
    public String consumir(@RequestParam Integer idCita,
            @RequestParam Integer idRepuesto,
            @RequestParam Integer cantidad) {
        inventarioService.consumir(idCita, idRepuesto, cantidad);
        return "redirect:/cita/" + idCita + "/detalle";
    }
}
