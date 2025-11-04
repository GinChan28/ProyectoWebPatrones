/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.service;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.MecanicoRepository;
import com.taller_mecanico.repository.ServicioRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * 
 */
@Service
public class CitaService {

    private final CitaRepository citaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final ServicioRepository servicioRepo;
    private final MecanicoRepository mecanicoRepo;
    private final HistorialEstadoService historialService;

    public CitaService(
            CitaRepository citaRepo,
            VehiculoRepository vehiculoRepo,
            ServicioRepository servicioRepo,
            MecanicoRepository mecanicoRepo,
            HistorialEstadoService historialService
    ) 
    {
        this.citaRepo = citaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.servicioRepo = servicioRepo;
        this.mecanicoRepo = mecanicoRepo;
        this.historialService = historialService;
    }

    public Cita crearCita(Integer idVehiculo, Integer idServicio, LocalDate fecha, LocalTime hora) {
        var cita = new Cita();
        cita.setVehiculo(vehiculoRepo.findById(idVehiculo).orElseThrow());
        cita.setServicio(servicioRepo.findById(idServicio).orElseThrow());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(Cita.Estado.PENDIENTE);
        return citaRepo.save(cita);
    }

    public Cita asignarMecanico(Integer idCita, Integer idMecanico) {
        var cita = citaRepo.findById(idCita).orElseThrow();
        cita.setMecanico(mecanicoRepo.findById(idMecanico).orElseThrow());
        return citaRepo.save(cita);
    }

    public Cita cambiarEstado(Integer idCita, Cita.Estado nuevo) {
        var cita = citaRepo.findById(idCita).orElseThrow();
        var anterior = cita.getEstado();
        if (anterior == nuevo) {
            return cita; // nada que registrar
        }
        cita.setEstado(nuevo);
        var saved = citaRepo.save(cita);
        historialService.registrarCambio(saved, anterior, nuevo);
        return saved;
    }

    public List<Cita> listarTodas() {
        return citaRepo.findAll();
    }

    public Cita obtener(Integer id) {
        return citaRepo.findById(id).orElseThrow();
    }
}
