package com.taller_mecanico.service;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Mecanico;
import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.MecanicoRepository;
import com.taller_mecanico.repository.ServicioRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CitaService {

    private final CitaRepository citaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final ServicioRepository servicioRepo;
    private final MecanicoRepository mecanicoRepo;
    private final HistorialEstadoService historialService;
    private final ClienteRepository clienteRepo;

    public CitaService(
            CitaRepository citaRepo,
            VehiculoRepository vehiculoRepo,
            ServicioRepository servicioRepo,
            MecanicoRepository mecanicoRepo,
            HistorialEstadoService historialService,
            ClienteRepository clienteRepo
    ) {
        this.citaRepo = citaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.servicioRepo = servicioRepo;
        this.mecanicoRepo = mecanicoRepo;
        this.historialService = historialService;
        this.clienteRepo = clienteRepo;
    }

    // ---------------------------------------------------------
    // CREAR CITA (para ADMIN / CONTROL GENERAL)
    // ---------------------------------------------------------
    public Cita crearCita(Integer idVehiculo, Integer idServicio,
                          LocalDate fecha, LocalTime hora) {

        var cita = new Cita();
        cita.setVehiculo(vehiculoRepo.findById(idVehiculo).orElseThrow());
        cita.setServicio(servicioRepo.findById(idServicio).orElseThrow());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(Cita.Estado.PENDIENTE);
        cita.setMecanico(null);
        cita.setComentario(null);

        return citaRepo.save(cita);
    }

    // ---------------------------------------------------------
    // CREAR CITA DESDE CLIENTE (usa strings del formulario)
    // ---------------------------------------------------------
    public void crearCitaCliente(Integer idVehiculo, Integer idServicio,
                                 String fecha, String hora) {

        var vehiculo = vehiculoRepo.findById(idVehiculo).orElseThrow();
        var servicio = servicioRepo.findById(idServicio).orElseThrow();

        Cita cita = new Cita();
        cita.setVehiculo(vehiculo);
        cita.setServicio(servicio);
        cita.setFecha(LocalDate.parse(fecha));
        cita.setHora(LocalTime.parse(hora));
        cita.setEstado(Cita.Estado.PENDIENTE);
        cita.setMecanico(null);
        cita.setComentario(null);

        citaRepo.save(cita);
    }

    // ---------------------------------------------------------
    // ASIGNAR MECÁNICO (ADMIN u otro flujo manual)
    // ---------------------------------------------------------
    public Cita asignarMecanico(Integer idCita, Integer idMecanico) {
        var cita = citaRepo.findById(idCita).orElseThrow();
        cita.setMecanico(mecanicoRepo.findById(idMecanico).orElseThrow());
        return citaRepo.save(cita);
    }

    // ---------------------------------------------------------
    // CAMBIAR ESTADO GENERICO (sin comentario)
    // ---------------------------------------------------------
    public Cita cambiarEstado(Integer idCita, Cita.Estado nuevo) {
        return cambiarEstadoConComentario(idCita, nuevo, null);
    }

    // ---------------------------------------------------------
    // CAMBIAR ESTADO CON COMENTARIO
    // ---------------------------------------------------------
    public Cita cambiarEstadoConComentario(Integer idCita,
                                           Cita.Estado nuevo,
                                           String comentario) {

        var cita = citaRepo.findById(idCita).orElseThrow();
        var anterior = cita.getEstado();

        if (anterior == nuevo) {
            // nada que registrar
            return cita;
        }

        // Comentario opcional: se acumula al existente
        if (comentario != null && !comentario.isBlank()) {
            String existente = cita.getComentario();
            if (existente == null || existente.isBlank()) {
                cita.setComentario(comentario.trim());
            } else {
                cita.setComentario(existente + "\n" + comentario.trim());
            }
        }

        cita.setEstado(nuevo);
        var guardada = citaRepo.save(cita);

        historialService.registrarCambio(guardada, anterior, nuevo);

        return guardada;
    }

    // ---------------------------------------------------------
    // FLUJO ESPECÍFICO PARA MECÁNICO
    // ---------------------------------------------------------

    /**
     * Mecánico toma una cita pendiente: se asigna a él
     * y pasa a EN_PROGRESO. Puede dejar un comentario inicial.
     */
    public Cita tomarCitaPorMecanico(String usernameMecanico,
                                     Integer idCita,
                                     String comentario) {

        var cita = citaRepo.findById(idCita).orElseThrow();

        // Solo se puede tomar si está pendiente
        if (cita.getEstado() != Cita.Estado.PENDIENTE) {
            return cita;
        }

        Mecanico mecanico = mecanicoRepo.findByUsuarioUsername(usernameMecanico);
        if (mecanico == null) {
            throw new IllegalStateException("Mecánico no encontrado para usuario: " + usernameMecanico);
        }

        cita.setMecanico(mecanico);

        return cambiarEstadoConComentario(idCita, Cita.Estado.EN_PROGRESO, comentario);
    }

    /**
     * Mecánico finaliza una cita que está EN_PROGRESO.
     */
    public Cita finalizarCitaPorMecanico(Integer idCita, String comentario) {

        var cita = citaRepo.findById(idCita).orElseThrow();

        if (cita.getEstado() != Cita.Estado.EN_PROGRESO) {
            return cita;
        }

        return cambiarEstadoConComentario(idCita, Cita.Estado.FINALIZADA, comentario);
    }

    /**
     * Cancelar cita (por admin o mecánico), guardando motivo.
     */
    public Cita cancelarCita(Integer idCita, String comentario) {
        return cambiarEstadoConComentario(idCita, Cita.Estado.CANCELADA, comentario);
    }

    // ---------------------------------------------------------
    // LISTAR TODAS LAS CITAS (ADMIN)
    // ---------------------------------------------------------
    public List<Cita> listarTodas() {
        return citaRepo.findAll();
    }

    // ---------------------------------------------------------
    // OBTENER UNA CITA POR ID
    // ---------------------------------------------------------
    public Cita obtener(Integer id) {
        return citaRepo.findById(id).orElseThrow();
    }

    // ---------------------------------------------------------
    // LISTAR CITAS POR CLIENTE (Cliente → Vehículos → Citas)
    // ---------------------------------------------------------
    public List<Cita> listarCitasPorCliente(String username) {

Cliente cliente = clienteRepo.findByUsuarioUsername(username)
        .orElseThrow(() ->
            new IllegalStateException("Cliente no encontrado para el usuario: " + username)
        );

        if (cliente == null) {
            return List.of();
        }

        var vehiculos = vehiculoRepo.findByClienteIdCliente(cliente.getIdCliente());

        List<Cita> citasCliente = new ArrayList<>();

        for (Vehiculo v : vehiculos) {
            citasCliente.addAll(citaRepo.findByVehiculoIdVehiculo(v.getIdVehiculo()));
        }

        return citasCliente;
    }

    // ---------------------------------------------------------
    // LISTAR CITAS ASIGNADAS A UN MECÁNICO
    // ---------------------------------------------------------
    public List<Cita> listarCitasAsignadas(String username) {

        Mecanico mecanico = mecanicoRepo.findByUsuarioUsername(username);

        if (mecanico == null) {
            return List.of();
        }

        return citaRepo.findByMecanicoIdMecanico(mecanico.getIdMecanico());
    }
    
       public List<Cita> listarPorEstado(Cita.Estado estado) {
        return citaRepo.findByEstado(estado);
    }

    // ---------------------------------------------------------
    // LISTAR CITAS POR FECHA EXACTA (ADMIN)
    // ---------------------------------------------------------
    public List<Cita> buscarPorFecha(LocalDate fecha) {
        return citaRepo.findByFechaBetween(fecha, fecha);
    }
    
    
    
     public List<Cita> historialServicios(
            Integer idCliente,
            LocalDate fecha,
            String servicio
    ) {

        if (fecha != null) {
            return citaRepo.findByVehiculoClienteIdClienteAndEstadoAndFecha(
                    idCliente,
                    Cita.Estado.FINALIZADA,
                    fecha
            );
        }

        if (servicio != null && !servicio.isBlank()) {
            return citaRepo
                    .findByVehiculoClienteIdClienteAndEstadoAndServicioNombreContainingIgnoreCase(
                            idCliente,
                            Cita.Estado.FINALIZADA,
                            servicio
                    );
        }

        return citaRepo.findByVehiculoClienteIdClienteAndEstado(
                idCliente,
                Cita.Estado.FINALIZADA
        );
    }
     
public void cancelarCitaCliente(Integer idCita, String username) {

    Cita cita = citaRepo.findById(idCita)
            .orElseThrow(() -> new IllegalStateException("Cita no encontrada"));

    // validación básica
    if (cita.getEstado() != Cita.Estado.PENDIENTE) {
        throw new IllegalStateException("Solo se pueden cancelar citas pendientes");
    }

    cita.setEstado(Cita.Estado.CANCELADA);
    cita.setComentario("Cancelada por el cliente");

    citaRepo.save(cita);
}


@Transactional
public void reprogramarCita(Integer idCita,
                            LocalDate fecha,
                            LocalTime hora,
                            String username) {

    Cita cita = obtenerCitaCliente(idCita, username);

    if (cita.getEstado() != Cita.Estado.PENDIENTE) {
        throw new IllegalStateException("Solo se pueden reprogramar citas pendientes");
    }

    if (fecha.isBefore(LocalDate.now())) {
        throw new IllegalStateException("Fecha inválida");
    }

    cita.setFecha(fecha);
    cita.setHora(hora);
    citaRepo.save(cita);
}
public Cita obtenerCitaCliente(Integer idCita, String username) {

    Cliente cliente = clienteRepo.findByUsuarioUsername(username)
            .orElseThrow(() ->
                    new IllegalStateException("Cliente no encontrado")
            );

    Cita cita = citaRepo.findById(idCita)
            .orElseThrow(() ->
                    new IllegalStateException("Cita no encontrada")
            );

    // Validar que la cita sea del cliente
    Integer idClienteCita = cita.getVehiculo().getCliente().getIdCliente();
    if (!idClienteCita.equals(cliente.getIdCliente())) {
        throw new IllegalStateException("No tienes permiso sobre esta cita");
    }

    return cita;
}

    
}
