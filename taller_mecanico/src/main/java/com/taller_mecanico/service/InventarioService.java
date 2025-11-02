/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.service;

import com.taller_mecanico.domain.ConsumoRepuesto;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.ConsumoRepuestoRepository;
import com.taller_mecanico.repository.RepuestoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 *
 * @author megan
 */
@Service
public class InventarioService {

    private final RepuestoRepository repuestoRepo;
    private final ConsumoRepuestoRepository consumoRepo;
    private final CitaRepository citaRepo;

    public InventarioService(RepuestoRepository repuestoRepo,ConsumoRepuestoRepository consumoRepo,CitaRepository citaRepo) {
        this.repuestoRepo = repuestoRepo;
        this.consumoRepo = consumoRepo;
        this.citaRepo = citaRepo;
    }

    @Transactional
    public ConsumoRepuesto consumir(Integer idCita, Integer idRepuesto, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        var cita = citaRepo.findById(idCita).orElseThrow();
        var rep = repuestoRepo.findById(idRepuesto).orElseThrow();

        if (rep.getExistencias() == null || rep.getExistencias() < cantidad) {
            throw new IllegalStateException("No hay existencias suficientes");
        }

        var consumoOpt = consumoRepo.findByCitaIdCitaAndRepuestoIdRepuesto(idCita, idRepuesto);
        ConsumoRepuesto consumo = consumoOpt.orElseGet(() -> {
            var c = new ConsumoRepuesto();
            c.setCita(cita);
            c.setRepuesto(rep);
            c.setCantidad(0);
            return c;
        });

        consumo.setCantidad(consumo.getCantidad() + cantidad);
        rep.setExistencias(rep.getExistencias() - cantidad);

        repuestoRepo.save(rep);
        return consumoRepo.save(consumo);
    }
}
