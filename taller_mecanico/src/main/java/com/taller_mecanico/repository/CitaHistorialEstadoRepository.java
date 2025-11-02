/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.CitaHistorialEstado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface CitaHistorialEstadoRepository extends JpaRepository<CitaHistorialEstado, Integer> {

    List<CitaHistorialEstado> findByCitaIdCitaOrderByCambiadoEnAsc(Integer idCita);
}
