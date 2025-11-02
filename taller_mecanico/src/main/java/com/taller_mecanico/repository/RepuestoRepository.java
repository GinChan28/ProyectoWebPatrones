/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Repuesto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface RepuestoRepository extends JpaRepository<Repuesto, Integer> {

    List<Repuesto> findByActivoTrue();

    List<Repuesto> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);

    Optional<Repuesto> findBySku(String sku);
}
