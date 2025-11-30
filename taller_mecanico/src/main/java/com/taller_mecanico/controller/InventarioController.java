package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Repuesto;
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

    @GetMapping("/repuestos")
    public String mostrarRepuestos(Model model) {
        model.addAttribute("repuestos", repuestoRepository.findAll());
        return "inventario/repuestos";
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("repuesto", new Repuesto());  
        return "inventario/formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editarRepuesto(@PathVariable Integer id, Model model) {
        Repuesto repuesto = repuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado"));
        model.addAttribute("repuesto", repuesto);
        return "inventario/formulario";
    }

    @PostMapping("/guardar")
public String guardarRepuesto(@ModelAttribute Repuesto repuesto, Model model) {

    Repuesto existente = repuestoRepository.findByNombreOrSku(
            repuesto.getNombre(), repuesto.getSku()
    );

    if (existente != null && 
       (repuesto.getIdRepuesto() == null || 
        !existente.getIdRepuesto().equals(repuesto.getIdRepuesto()))) {

        model.addAttribute("repuesto", repuesto);
        model.addAttribute("error", "Ya existe un repuesto con ese nombre o SKU.");
        return "inventario/formulario";
    }

    repuestoRepository.save(repuesto);
    return "redirect:/inventario/repuestos";
}

    @GetMapping("/eliminar/{id}")
    public String eliminarRepuesto(@PathVariable Integer id) {
        repuestoRepository.deleteById(id);
        return "redirect:/inventario/repuestos";
    }

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
            return "redirect:/cita/" + idCita + "/detalle?error=true";
        }
    }
}
