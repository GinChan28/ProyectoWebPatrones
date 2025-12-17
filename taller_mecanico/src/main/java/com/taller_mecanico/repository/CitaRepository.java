/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Cita;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 *
 * @author megan
 */
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByMecanicoIdMecanico(Integer idMecanico);

    List<Cita> findByVehiculoIdVehiculo(Integer idVehiculo);

    List<Cita> findByEstado(Cita.Estado estado);
    
     List<Cita> findByMecanicoIdMecanicoAndEstado(
        Integer idMecanico,
                     Cita.Estado estado
     );
    List<Cita> findByFechaBetween(LocalDate desde, LocalDate hasta);
    
    List<Cita> findByVehiculoClienteIdClienteAndEstado(
        Integer idCliente,
        Cita.Estado estado
);

List<Cita> findByVehiculoClienteIdClienteAndEstadoAndServicioNombreContainingIgnoreCase(
        Integer idCliente,
        Cita.Estado estado,
        String servicio
);

List<Cita> findByVehiculoClienteIdClienteAndEstadoAndFecha(
        Integer idCliente,
        Cita.Estado estado,
        LocalDate fecha
);

    
   
}
