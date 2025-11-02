/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.service;

import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.VehiculoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author megan
 */
@Service
public class VehiculoService {

    private final VehiculoRepository repo;

    public VehiculoService(VehiculoRepository repo) {
        this.repo = repo;
    }

    public List<Vehiculo> porCliente(Integer idCliente) {
        return repo.findByClienteIdCliente(idCliente);
    }

    public Vehiculo guardar(Vehiculo v) {
        return repo.save(v);
    }
}
