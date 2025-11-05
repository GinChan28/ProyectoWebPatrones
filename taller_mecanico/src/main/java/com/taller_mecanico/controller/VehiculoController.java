package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    private final VehiculoRepository vehiculoRepo;
    private final ClienteRepository clienteRepo;

    public VehiculoController(VehiculoRepository vehiculoRepo, ClienteRepository clienteRepo) {
        this.vehiculoRepo = vehiculoRepo;
        this.clienteRepo = clienteRepo;
    }

    // DEMO: usa el primer cliente de la BD mientras no integramos login
    private Integer idClienteDemo() {
        return clienteRepo.findAll().stream()
                .findFirst()
                .map(Cliente::getIdCliente)
                .orElseThrow(() -> new IllegalStateException("No hay clientes en BD"));
    }

    @GetMapping("/listado")
    public String mostrarListado(Model model) {
        model.addAttribute("vehiculos", vehiculoRepo.findByClienteIdCliente(idClienteDemo()));
        return "vehiculo/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "vehiculo/formulario";
    }

    @GetMapping("/mis")
    public String misVehiculos(Model model) {
        model.addAttribute("vehiculos", Optional.ofNullable(
                vehiculoRepo.findByClienteIdCliente(idClienteDemo())
        ).orElse(List.of()));
        return "vehiculo/mis";
    }

    @PostMapping("/guardar")
    public String guardarVehiculo(@ModelAttribute Vehiculo vehiculo) {
        Cliente cliente = clienteRepo.findById(idClienteDemo())
                .orElseThrow(() -> new IllegalStateException("Cliente demo no encontrado"));
        vehiculo.setCliente(cliente);
        vehiculoRepo.save(vehiculo);
        return "redirect:/vehiculo/listado";
    }

    @GetMapping("/editar/{id}")
    public String editarVehiculo(@PathVariable Integer id, Model model) {
        Vehiculo vehiculo = vehiculoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + id));
        model.addAttribute("vehiculo", vehiculo);
        return "vehiculo/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarVehiculo(@ModelAttribute Vehiculo vehiculo) {
        Cliente cliente = clienteRepo.findById(idClienteDemo())
                .orElseThrow(() -> new IllegalStateException("Cliente demo no encontrado"));
        vehiculo.setCliente(cliente);
        vehiculoRepo.save(vehiculo);
        return "redirect:/vehiculo/listado";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarVehiculo(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        try {
            vehiculoRepo.deleteById(id);
            redirectAttrs.addFlashAttribute("exito", "Vehículo eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", "No se puede eliminar el vehículo porque tiene citas asociadas.");
        }
        return "redirect:/vehiculo/listado";
    }
}