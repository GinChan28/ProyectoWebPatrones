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

    public CitaController(CitaService c, VehiculoRepository v, ServicioRepository s, MecanicoRepository m,
                          ClienteRepository cr, ArchivoRepository ar, CitaHistorialEstadoRepository hr,
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
        return "cita/listado";
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
        return "redirect:/cita/lista";
    }

    @GetMapping("/{id}/asignar")
    public String asignarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("idCita", id);
        model.addAttribute("mecanicos", mecanicoRepo.findAll());
        return "cita/asignar";
    }

    @PostMapping("/{id}/asignar")
    public String asignar(@PathVariable Integer id, @RequestParam Integer idMecanico) {
        citaService.asignarMecanico(id, idMecanico);
        return "redirect:/cita/lista";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam Cita.Estado estado) {
        citaService.cambiarEstado(id, estado);
        return "redirect:/cita/lista";
    }

    @GetMapping("/{id}/detalle")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("cita", citaService.obtener(id));
        model.addAttribute("archivos", archivoRepo.findByCitaIdCita(id));
        model.addAttribute("historial", historialRepo.findByCitaIdCitaOrderByCambiadoEnAsc(id));
        model.addAttribute("repuestos", repuestoRepo.findAll());
        return "cita/detalle";
    }

    
   @GetMapping("/{id}/evidencia")
public String evidencia(@PathVariable Integer id, Model model) {
    model.addAttribute("cita", citaService.obtener(id));
    model.addAttribute("archivos", archivoService.listarPorCita(id));
    model.addAttribute("historial", historialRepo.findByCitaIdCitaOrderByCambiadoEnAsc(id));
    model.addAttribute("repuestos", repuestoRepo.findAll());
    return "cita/detalle";
}


    @PostMapping("/{id}/evidencia")
    public String subirEvidencia(
            @PathVariable("id") Integer idCita,
            @RequestParam("archivo") MultipartFile archivo,
            Model model) {

        try {
            archivoService.subirEvidenciaDeCita(idCita, archivo);
            return "redirect:/cita/" + idCita + "/detalle";

        } catch (Exception e) {

            var cita = citaService.obtener(idCita);
            var historial = historialRepo.findByCitaIdCitaOrderByCambiadoEnAsc(idCita);
            var repuestos = repuestoRepo.findAll();
            var archivos = archivoService.listarPorCita(idCita);

            model.addAttribute("cita", cita);
            model.addAttribute("historial", historial);
            model.addAttribute("repuestos", repuestos);
            model.addAttribute("archivos", archivos);
            model.addAttribute("errorMensaje", "Error al subir evidencia: " + e.getMessage());

            return "cita/detalle";
        }
    }

}
