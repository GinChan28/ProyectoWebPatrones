/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.ConsumoRepuesto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface ConsumoRepuestoRepository extends JpaRepository<ConsumoRepuesto, Integer> {

    List<ConsumoRepuesto> findByCitaIdCita(Integer idCita);

    Optional<ConsumoRepuesto> findByCitaIdCitaAndRepuestoIdRepuesto(Integer idCita, Integer idRepuesto);
}
