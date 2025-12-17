package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.domain.Mecanico;
import com.taller_mecanico.service.CitaService;
import com.taller_mecanico.service.MecanicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.taller_mecanico.domain.Cliente;
import java.util.List;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.repository.ClienteRepository;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CitaService citaService;
    private final MecanicoService mecanicoService;
    private final ClienteRepository clienteRepo;

   public AdminController(CitaService citaService,
                       MecanicoService mecanicoService,
                       ClienteRepository clienteRepo) {
    this.citaService = citaService;
    this.mecanicoService = mecanicoService;
    this.clienteRepo = clienteRepo;
}


    // =========================
    // DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String dashboardAdmin() {
        return "admin/dashboard";
    }

    // =========================
    // ASIGNAR CITAS
    // =========================
    @GetMapping("/asignar-citas")
    public String asignarCitas(Model model) {
        model.addAttribute("citas", citaService.listarPorEstado(Cita.Estado.PENDIENTE));
        model.addAttribute("mecanicos", mecanicoService.listarDisponibles());
        return "admin/asignar-citas";
    }

    @PostMapping("/asignar-citas")
    public String asignarCita(@RequestParam Integer idCita,
                              @RequestParam Integer idMecanico) {

        citaService.asignarMecanico(idCita, idMecanico);
        citaService.cambiarEstado(idCita, Cita.Estado.EN_PROGRESO);
        return "redirect:/admin/asignar-citas";
    }

    // =========================
    // CITAS POR FECHA
    // =========================
    @GetMapping("/citas")
    public String listarCitasPorFecha(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        if (fecha != null) {
            model.addAttribute("citas", citaService.buscarPorFecha(fecha));
            model.addAttribute("fechaSeleccionada", fecha);
        }
        return "admin/citas";
    }

    // =========================
    // GESTIÓN DE MECÁNICOS
    // =========================
    @GetMapping("/mecanicos")
    public String gestionMecanicos(Model model) {
        model.addAttribute("mecanicos", mecanicoService.listarTodos());
        return "admin/mecanicos";
    }

    // FORMULARIO NUEVO
    @GetMapping("/mecanicos/nuevo")
    public String nuevoMecanico(Model model) {
        model.addAttribute("mecanico", new Mecanico());
        return "admin/mecanico-form";
    }

    // GUARDAR (CREAR o EDITAR)
    @PostMapping("/mecanicos/guardar")
    public String guardarMecanico(@ModelAttribute Mecanico mecanico,
                                  Model model) {

        try {
            mecanicoService.guardar(mecanico);
            return "redirect:/admin/mecanicos";

        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mecanico", mecanico);
            return "admin/mecanico-form";
        }
    }

    // EDITAR
    @GetMapping("/mecanicos/editar/{id}")
    public String editarMecanico(@PathVariable Integer id, Model model) {
        model.addAttribute("mecanico", mecanicoService.obtener(id));
        return "admin/mecanico-form";
    }

    // ELIMINAR
    @GetMapping("/mecanicos/eliminar/{id}")
    public String eliminarMecanico(@PathVariable Integer id, Model model) {
        try {
            mecanicoService.eliminar(id);
        } catch (IllegalStateException e) {
            // si tiene citas activas, mostramos un mensaje de error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mecanicos", mecanicoService.listarTodos());
            return "admin/mecanicos";
        }
        return "redirect:/admin/mecanicos";
    }
    
@GetMapping("/clientes")
public String listarClientes(
        @RequestParam(required = false) String placa,
        Model model) {

    List<Cliente> clientes = clienteRepo.findAll();

    if (placa != null && !placa.isBlank()) {
        clientes = clientes.stream()
                .filter(c -> c.getVehiculos().stream()
                        .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa)))
                .peek(c -> c.setVehiculos(
                        c.getVehiculos().stream()
                                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                                .toList()
                ))
                .toList();
    }

    model.addAttribute("clientes", clientes);
    model.addAttribute("placa", placa);

    return "admin/clientes";
}



}
