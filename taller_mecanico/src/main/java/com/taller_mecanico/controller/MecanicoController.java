package com.taller_mecanico.controller;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.service.CitaService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MecanicoController {

    private final CitaService citaService;

    public MecanicoController(CitaService citaService) {
        this.citaService = citaService;
    }

    // ====================== UTILIDAD ROLES ===========================
    private boolean tieneRol(Authentication auth, String rol) {
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_" + rol));
    }

    // ====================== DASHBOARD MECÁNICO =======================
    @GetMapping("/mecanico/dashboard")
    public String dashboardMecanico(Authentication auth) {

        if (!(tieneRol(auth, "MECANICO") || tieneRol(auth, "ADMIN"))) {
            return "redirect:/login?error";
        }

        return "mecanico/dashboard"; // templates/mecanico/dashboard.html
    }

    // ====================== LISTA DE CITAS ===========================
    @GetMapping("/mecanico/citas")
    public String verCitasMecanico(Authentication auth, Model model) {

        if (!(tieneRol(auth, "MECANICO") || tieneRol(auth, "ADMIN"))) {
            return "redirect:/login?error";
        }

        String username = auth.getName();

        // Citas asignadas al mecánico logueado
        List<Cita> asignadas = citaService.listarCitasAsignadas(username);
        if (asignadas == null) {
            asignadas = List.of();
        }

        // Citas pendientes sin mecánico asignado
        List<Cita> pendientes = citaService.listarTodas().stream()
                .filter(c -> c.getEstado() == Cita.Estado.PENDIENTE
                        && c.getMecanico() == null)
                .toList();

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("asignadas", asignadas);

        return "mecanico/citas"; // templates/mecanico/citas.html
    }

    // ====================== TOMAR CITA PENDIENTE =====================
    @PostMapping("/mecanico/cita/tomar")
    public String tomarCita(@RequestParam("idCita") Integer idCita,
                            @RequestParam(value = "comentario", required = false) String comentario,
                            Authentication auth,
                            RedirectAttributes redirectAttrs) {

        if (!tieneRol(auth, "MECANICO")) {
            redirectAttrs.addFlashAttribute("error",
                    "Solo los mecánicos pueden tomar citas.");
            return "redirect:/mecanico/citas";
        }

        try {
            String username = auth.getName();
            citaService.tomarCitaPorMecanico(username, idCita, comentario);
            redirectAttrs.addFlashAttribute("exito",
                    "La cita fue tomada correctamente.");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error",
                    "No se pudo tomar la cita: " + ex.getMessage());
        }

        return "redirect:/mecanico/citas";
    }

    // ====================== FINALIZAR CITA ===========================
    @PostMapping("/mecanico/cita/finalizar")
    public String finalizarCita(@RequestParam("idCita") Integer idCita,
                                @RequestParam(value = "comentario", required = false) String comentario,
                                Authentication auth,
                                RedirectAttributes redirectAttrs) {

        if (!tieneRol(auth, "MECANICO")) {
            redirectAttrs.addFlashAttribute("error",
                    "Solo los mecánicos pueden finalizar citas.");
            return "redirect:/mecanico/citas";
        }

        try {
            citaService.finalizarCitaPorMecanico(idCita, comentario);
            redirectAttrs.addFlashAttribute("exito",
                    "La cita fue marcada como FINALIZADA.");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error",
                    "No se pudo finalizar la cita: " + ex.getMessage());
        }

        return "redirect:/mecanico/citas";
    }

    // ====================== CANCELAR CITA ============================
    @PostMapping("/mecanico/cita/cancelar")
    public String cancelarCita(@RequestParam("idCita") Integer idCita,
                               @RequestParam(value = "comentario", required = false) String comentario,
                               Authentication auth,
                               RedirectAttributes redirectAttrs) {

        if (!(tieneRol(auth, "MECANICO") || tieneRol(auth, "ADMIN"))) {
            redirectAttrs.addFlashAttribute("error",
                    "Solo mecánicos o administradores pueden cancelar citas.");
            return "redirect:/mecanico/citas";
        }

        try {
            citaService.cancelarCita(idCita, comentario);
            redirectAttrs.addFlashAttribute("exito",
                    "La cita fue cancelada correctamente.");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error",
                    "No se pudo cancelar la cita: " + ex.getMessage());
        }

        return "redirect:/mecanico/citas";
    }
}
