package com.taller_mecanico.service;

import com.taller_mecanico.domain.Servicio;
import com.taller_mecanico.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepo;

    public ServicioService(ServicioRepository servicioRepo) {
        this.servicioRepo = servicioRepo;
    }

    public List<Servicio> listar() {
        return servicioRepo.findAll();
    }

    public Servicio buscarPorId(Integer id) {
        return servicioRepo.findById(id).orElse(null);
    }

    public void guardar(Servicio servicio) {
        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }
        servicioRepo.save(servicio);
    }

    public void eliminar(Integer id) {
        servicioRepo.deleteById(id);
    }
}
