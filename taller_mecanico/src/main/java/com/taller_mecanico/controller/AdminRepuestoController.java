package com.taller_mecanico.controller.admin;

import com.taller_mecanico.domain.Repuesto;
import com.taller_mecanico.service.RepuestoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/repuestos")
public class AdminRepuestoController {

    private final RepuestoService repuestoService;

    public AdminRepuestoController(RepuestoService repuestoService) {
        this.repuestoService = repuestoService;
    }

    // LISTADO
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("repuestos", repuestoService.listarTodos());
        return "admin/repuestos";
    }

    // FORM NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("repuesto", new Repuesto());
        return "admin/repuesto-form";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Repuesto repuesto) {
        repuestoService.guardar(repuesto);
        return "redirect:/admin/repuestos";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("repuesto", repuestoService.buscarPorId(id));
        return "admin/repuesto-form";
    }

    // ACTIVAR / DESACTIVAR
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Integer id) {
        repuestoService.toggleActivo(id);
        return "redirect:/admin/repuestos";
    }
}
