package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VehiculoController {

    private final VehiculoRepository vehiculoRepo;
    private final ClienteRepository clienteRepo;

    public VehiculoController(VehiculoRepository vehiculoRepo, ClienteRepository clienteRepo) {
        this.vehiculoRepo = vehiculoRepo;
        this.clienteRepo = clienteRepo;
    }

    // =====================================================================
    // Utilidad: verificar rol
    // =====================================================================
    private boolean tieneRol(Authentication auth, String rol) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_" + rol));
    }

    // =====================================================================
    // LISTA GLOBAL (ADMIN / MECÁNICO)
    // =====================================================================
    @GetMapping("/vehiculos")
    public String listaGlobal(Authentication auth, Model model) {

        if (!(tieneRol(auth, "ADMIN") || tieneRol(auth, "MECANICO"))) {
            return "redirect:/cliente/mis-vehiculos";
        }

        model.addAttribute("vehiculos", vehiculoRepo.findAll());
        return "vehiculo/lista-global";
    }

    // =====================================================================
    // LISTA DEL CLIENTE (mis vehículos)
    // =====================================================================
    @GetMapping("/cliente/vehiculos")
    public String listaCliente(Authentication auth, Model model) {

        String username = auth.getName();
Cliente cliente = clienteRepo.findByUsuarioUsername(username)
        .orElseThrow(() ->
            new IllegalStateException("Cliente no encontrado para el usuario: " + username)
        );

        if (cliente == null) {
            model.addAttribute("vehiculos", List.of());
            return "cliente/mis-vehiculos";
        }

        List<Vehiculo> vehiculos = vehiculoRepo.findByClienteIdCliente(cliente.getIdCliente());
        model.addAttribute("vehiculos", vehiculos);

        return "cliente/mis-vehiculos";
    }

    // =====================================================================
    // NUEVO VEHÍCULO (CLIENTE / ADMIN)
    // =====================================================================
    @GetMapping("/cliente/vehiculo/nuevo")
    public String nuevoVehiculoCliente(Authentication auth, Model model) {

        if (!(tieneRol(auth, "CLIENTE") || tieneRol(auth, "ADMIN"))) {
            return "redirect:/vehiculos";
        }

        model.addAttribute("vehiculo", new Vehiculo());
        return "cliente/vehiculo-form";
    }

    // =====================================================================
    // GUARDAR NUEVO VEHÍCULO
    // =====================================================================
@PostMapping("/cliente/vehiculo/guardar")
public String guardarVehiculoCliente(
        @ModelAttribute Vehiculo vehiculo,
        Authentication auth) {

    String username = auth.getName();

    Cliente cliente = clienteRepo.findByUsuarioUsername(username)
            .orElseThrow(() ->
                new IllegalStateException("El usuario no tiene perfil de cliente")
            );

    vehiculo.setCliente(cliente);
    vehiculoRepo.save(vehiculo);

    return "redirect:/cliente/mis-vehiculos";
}


    // =====================================================================
    // EDITAR VEHÍCULO
    // =====================================================================
    @GetMapping("/vehiculo/editar/{id}")
    public String editarVehiculo(
            @PathVariable Integer id,
            Authentication auth,
            Model model) {

        Vehiculo vehiculo = vehiculoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        String username = auth.getName();

        // Cliente: solo si el vehículo es suyo
        if (tieneRol(auth, "CLIENTE")) {
            if (!vehiculo.getCliente().getUsuario().getUsername().equals(username)) {
                return "redirect:/cliente/mis-vehiculos";
            }
        }

        // Mecánico no edita
        if (tieneRol(auth, "MECANICO")) {
            return "redirect:/vehiculos";
        }

        model.addAttribute("vehiculo", vehiculo);
        // mismo formulario; el hidden idVehiculo decide si es edición
        return "cliente/vehiculo-form";
    }

    // =====================================================================
    // ACTUALIZAR VEHÍCULO
    // =====================================================================
    @PostMapping("/vehiculo/actualizar")
    public String actualizarVehiculo(
            @ModelAttribute Vehiculo vehiculo,
            Authentication auth) {

        String username = auth.getName();
        Vehiculo original = vehiculoRepo.findById(vehiculo.getIdVehiculo())
                .orElseThrow();

        // Cliente: solo si el vehículo original es suyo
        if (tieneRol(auth, "CLIENTE")) {
            if (!original.getCliente().getUsuario().getUsername().equals(username)) {
                return "redirect:/cliente/mis-vehiculos";
            }
        }

        // Mecánico no actualiza
        if (tieneRol(auth, "MECANICO")) {
            return "redirect:/vehiculos";
        }

        // Mantener el mismo cliente dueño
        vehiculo.setCliente(original.getCliente());
        vehiculoRepo.save(vehiculo);

        if (tieneRol(auth, "CLIENTE")) {
            return "redirect:/cliente/mis-vehiculos";
        }
        return "redirect:/vehiculos";
    }

    // =====================================================================
    // ELIMINAR VEHÍCULO (solo ADMIN)
    // =====================================================================
@PostMapping("/vehiculo/eliminar/{id}")
    public String eliminarVehiculo(
            @PathVariable Integer id,
            Authentication auth,
            RedirectAttributes redirectAttrs) {

        // Recuperar vehículo
        Vehiculo vehiculo = vehiculoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        String username = auth.getName();

        // Mecánico nunca puede eliminar
        if (tieneRol(auth, "MECANICO")) {
            return "redirect:/vehiculos";
        }

        // Si es CLIENTE solo puede eliminar sus propios vehículos
        if (tieneRol(auth, "CLIENTE")) {
            if (!vehiculo.getCliente().getUsuario().getUsername().equals(username)) {
                return "redirect:/cliente/mis-vehiculos";
            }
        }

        try {
            vehiculoRepo.delete(vehiculo);
            redirectAttrs.addFlashAttribute("exito", "Vehículo eliminado correctamente.");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "No se puede eliminar. Tiene citas asociadas.");
        }

        // Si es cliente, vuelve a su lista; si es admin, a la lista global
        if (tieneRol(auth, "CLIENTE")) {
            return "redirect:/cliente/mis-vehiculos";
        }
        return "redirect:/vehiculos";
    }

}
