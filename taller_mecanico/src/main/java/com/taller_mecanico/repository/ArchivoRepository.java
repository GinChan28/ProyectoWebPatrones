/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Archivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {

    List<Archivo> findByCitaIdCita(Integer idCita);

    List<Archivo> findByVehiculoIdVehiculo(Integer idVehiculo);
}
