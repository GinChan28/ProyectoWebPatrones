package com.taller_mecanico.controller;

import com.taller_mecanico.repository.RepuestoRepository;
import com.taller_mecanico.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final RepuestoRepository repuestoRepository;
    private final InventarioService inventarioService;

    public InventarioController(RepuestoRepository repuestoRepository, InventarioService inventarioService) {
        this.repuestoRepository = repuestoRepository;
        this.inventarioService = inventarioService;
    }

    // Muestra todos los repuestos disponibles
    @GetMapping("/repuestos")
    public String mostrarRepuestos(Model model) {
        model.addAttribute("repuestos", repuestoRepository.findAll());
        return "inventario/repuestos";
    }

    // Consume un repuesto para una cita específica
    @PostMapping("/consumir")
    public String consumirRepuesto(
            @RequestParam("idCita") Integer idCita,
            @RequestParam("idRepuesto") Integer idRepuesto,
            @RequestParam("cantidad") Integer cantidad
    ) {
        try {
            inventarioService.consumir(idCita, idRepuesto, cantidad);
            return "redirect:/cita/" + idCita + "/detalle";
        } catch (Exception e) {
            // Puede loguear el error o redirigir a una página de error personalizada
            return "redirect:/cita/" + idCita + "/detalle?error=true";
        }
    }
}
