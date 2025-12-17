package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.ServicioRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import com.taller_mecanico.service.CitaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.taller_mecanico.service.CarritoService;


import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;



@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private CitaRepository citaRepo;

    @Autowired
    private ServicioRepository servicioRepo;

    @Autowired
    private CitaService citaService;
    
    @Autowired
    private CarritoService carritoService;



    // --------------------------------------------------------
    // DASHBOARD DEL CLIENTE
    // --------------------------------------------------------
    @GetMapping("/dashboard")
    public String dashboard() {
        return "cliente/dashboard";   // <-- CORRECTO
    }


    // --------------------------------------------------------
    // LISTAR VEHÍCULOS DEL CLIENTE LOGUEADO
    // --------------------------------------------------------
    @GetMapping("/mis-vehiculos")
    public String misVehiculos(Model model, Authentication auth) {

        String username = auth.getName();

Cliente cliente = clienteRepo.findByUsuarioUsername(username)
        .orElseThrow(() ->
            new IllegalStateException("Cliente no encontrado para el usuario: " + username)
        );

        if (cliente == null) {
            model.addAttribute("vehiculos", List.of());
            return "cliente/mis-vehiculos";  // <-- CORRECTO
        }

        var vehiculos = vehiculoRepo.findByClienteIdCliente(cliente.getIdCliente());
        model.addAttribute("vehiculos", vehiculos);

        return "cliente/mis-vehiculos";  // <-- CORRECTO
    }


    // --------------------------------------------------------
    // LISTAR CITAS DEL CLIENTE
    // --------------------------------------------------------
    @GetMapping("/mis-citas")
    public String misCitas(Model model, Authentication auth) {

        String username = auth.getName();

Cliente cliente = clienteRepo.findByUsuarioUsername(username)
        .orElseThrow(() ->
            new IllegalStateException("Cliente no encontrado para el usuario: " + username)
        );

        if (cliente == null) {
            model.addAttribute("citas", List.of());
            return "cliente/mis-citas";  // <-- CORRECTO
        }

        var vehiculos = vehiculoRepo.findByClienteIdCliente(cliente.getIdCliente());

        List<Cita> citasCliente = new ArrayList<>();

        for (var v : vehiculos) {
            citasCliente.addAll(citaRepo.findByVehiculoIdVehiculo(v.getIdVehiculo()));
        }

        model.addAttribute("citas", citasCliente);

        return "cliente/mis-citas";  // <-- CORRECTO
    }


    // --------------------------------------------------------
    // FORMULARIO PARA AGENDAR CITA
    // --------------------------------------------------------
    @GetMapping("/agendar")
    public String agendar(Model model, Authentication auth) {

        String username = auth.getName();

Cliente cliente = clienteRepo.findByUsuarioUsername(username)
        .orElseThrow(() ->
            new IllegalStateException("Cliente no encontrado para el usuario: " + username)
        );

        var vehiculos = vehiculoRepo.findByClienteIdCliente(cliente.getIdCliente());

        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("servicios", servicioRepo.findAll());

        return "cliente/agendar";  // <-- CORRECTO
    }


    // --------------------------------------------------------
    // PROCESAR CITA CREADA POR UN CLIENTE
    // --------------------------------------------------------
    @PostMapping("/agendar")
    public String procesarCita(
            @RequestParam("vehiculoId") Integer vehiculoId,
            @RequestParam("servicioId") Integer servicioId,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora) {

        citaService.crearCitaCliente(
                vehiculoId,
                servicioId,
                fecha,
                hora
        );

        return "redirect:/cliente/mis-citas";  
    }
    @GetMapping("/repuestos")
public String verRepuestos(Model model) {

    model.addAttribute("repuestos", 
        carritoService.listarRepuestosDisponibles()
    );

    return "cliente/repuestos";
}

  
@PostMapping("/agregar")
public String agregar(
        @RequestParam Integer idRepuesto,
        @RequestParam Integer cantidad,
        Authentication auth
) {
    carritoService.agregarAlCarrito(
            auth.getName(),
            idRepuesto,
            cantidad
    );
    return "redirect:/cliente/carrito";
}

@GetMapping("/historial-servicios")
public String historialServicios(
        Authentication auth,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fecha,
        @RequestParam(required = false) String servicio,
        Model model
) {

    Cliente cliente = clienteRepo
            .findByUsuarioUsername(auth.getName())
            .orElseThrow(() ->
                    new IllegalStateException("Cliente no encontrado")
            );

    List<Cita> historial = citaService.historialServicios(
            cliente.getIdCliente(),
            fecha,
            servicio
    );

    model.addAttribute("historial", historial);
    model.addAttribute("fecha", fecha);
    model.addAttribute("servicio", servicio);

    return "cliente/historial-servicios";
}


@PostMapping("/citas/cancelar/{idCita}")
public String cancelarCita(@PathVariable Integer idCita,
                           Authentication auth,
                           RedirectAttributes ra) {

    try {
        citaService.cancelarCita(idCita, "Cancelada por el cliente");
        ra.addFlashAttribute("exito", "Cita cancelada correctamente");
    } catch (IllegalStateException e) {
        ra.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/cliente/mis-citas";
}



@GetMapping("/citas/reprogramar/{idCita}")
public String formReprogramar(@PathVariable Integer idCita,
                              Authentication auth,
                              Model model) {

    Cita cita = citaService.obtenerCitaCliente(idCita, auth.getName());

    model.addAttribute("cita", cita);
    return "cliente/reprogramar-cita";
}

@PostMapping("/citas/reprogramar")
public String reprogramar(@RequestParam Integer idCita,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
                          Authentication auth,
                          RedirectAttributes ra) {

    try {
        citaService.reprogramarCita(idCita, fecha, hora, auth.getName());
        ra.addFlashAttribute("msgExito", "Cita reprogramada correctamente");
    } catch (IllegalStateException e) {
        ra.addFlashAttribute("msgError", e.getMessage());
    }

    return "redirect:/cliente/mis-citas";
}



}


