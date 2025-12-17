package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Mecanico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MecanicoRepository extends JpaRepository<Mecanico, Integer> {

    List<Mecanico> findByEspecialidadContainingIgnoreCase(String especialidad);

    // Necesario para identificar al mecánico logueado
    Mecanico findByUsuarioUsername(String username);
}
