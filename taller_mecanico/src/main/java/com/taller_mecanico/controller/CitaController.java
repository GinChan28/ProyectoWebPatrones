/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.repository.*;
import com.taller_mecanico.service.ArchivoService;
import com.taller_mecanico.service.CitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author megan
 */
@Controller
@RequestMapping("/cita")
public class CitaController {

    private final CitaService citaService;
    private final VehiculoRepository vehiculoRepo;
    private final ServicioRepository servicioRepo;
    private final MecanicoRepository mecanicoRepo;
    private final ClienteRepository clienteRepo;
    private final ArchivoRepository archivoRepo;
    private final CitaHistorialEstadoRepository historialRepo;
    private final RepuestoRepository repuestoRepo;
    private final ArchivoService archivoService;

    public CitaController(CitaService c, VehiculoRepository v, ServicioRepository s, MecanicoRepository m, ClienteRepository cr, ArchivoRepository ar, CitaHistorialEstadoRepository hr,
            RepuestoRepository rr, ArchivoService as) {
        this.citaService = c;
        this.vehiculoRepo = v;
        this.servicioRepo = s;
        this.mecanicoRepo = m;
        this.clienteRepo = cr;
        this.archivoRepo = ar;
        this.historialRepo = hr;
        this.repuestoRepo = rr;
        this.archivoService = as;
    }

    private Integer idClienteDemo() {
        return clienteRepo.findAll().stream()
                .findFirst().map(x -> x.getIdCliente())
                .orElseThrow(() -> new IllegalStateException("No hay clientes en BD"));
    }

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "/cita/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("vehiculos", vehiculoRepo.findByClienteIdCliente(idClienteDemo()));
        model.addAttribute("servicios", servicioRepo.findByActivo(true));
        return "cita/nueva";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam Integer idVehiculo,
            @RequestParam Integer idServicio,
            @RequestParam String fecha,
            @RequestParam String hora) {
        citaService.crearCita(idVehiculo, idServicio, LocalDate.parse(fecha), LocalTime.parse(hora));
        return "/cita/listado";
    }

    @GetMapping("/{id}/asignar")
    public String asignarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("idCita", id);
        model.addAttribute("mecanicos", mecanicoRepo.findAll());
        return "/cita/asignar";
    }

    @PostMapping("/{id}/asignar")
    public String asignar(@PathVariable Integer id, @RequestParam Integer idMecanico) {
        citaService.asignarMecanico(id, idMecanico);
        return "redirect:/cita/listado";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam Cita.Estado estado) {
        citaService.cambiarEstado(id, estado);
        return "/cita/listado";
    }

    @GetMapping("/{id}/detalle")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("cita", citaService.obtener(id));
        model.addAttribute("archivos", archivoRepo.findByCitaIdCita(id));
        model.addAttribute("historial", historialRepo.findByCitaIdCitaOrderByCambiadoEnAsc(id));
        model.addAttribute("repuestos", repuestoRepo.findAll());
        return "/cita/detalle";
    }

    @PostMapping("/{id}/evidencia")
    public String subirEvidencia(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            archivoService.subirEvidenciaDeCita(id, file);
        }
        return "redirect:/cita/" + id + "/detalle";
    }
}
