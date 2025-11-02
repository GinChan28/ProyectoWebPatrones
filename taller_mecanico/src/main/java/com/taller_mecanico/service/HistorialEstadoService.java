/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.service;

import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.domain.CitaHistorialEstado;
import com.taller_mecanico.repository.CitaHistorialEstadoRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 *
 * @author megan
 */
@Service
public class HistorialEstadoService {

    private final CitaHistorialEstadoRepository repo;

    public HistorialEstadoService(CitaHistorialEstadoRepository repo) {
        this.repo = repo;
    }

    public void registrarCambio(Cita cita, Cita.Estado anterior, Cita.Estado nuevo) {
        CitaHistorialEstado c = new CitaHistorialEstado();
        c.setCita(cita);
        c.setEstadoAnterior(anterior);
        c.setEstadoNuevo(nuevo);
        c.setCambiadoEn(LocalDateTime.now());
        repo.save(c);
    }
}
