package com.taller_mecanico.service;

import com.taller_mecanico.domain.Repuesto;
import com.taller_mecanico.repository.RepuestoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepuestoService {

    private final RepuestoRepository repo;

    public RepuestoService(RepuestoRepository repo) {
        this.repo = repo;
    }

    public List<Repuesto> listarTodos() {
        return repo.findAll();
    }

    public Repuesto buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Repuesto no encontrado"));
    }

    public Repuesto guardar(Repuesto r) {
        return repo.save(r);
    }

    public void toggleActivo(Integer id) {
        Repuesto r = buscarPorId(id);
        r.setActivo(!r.getActivo());
        repo.save(r);
    }
}
