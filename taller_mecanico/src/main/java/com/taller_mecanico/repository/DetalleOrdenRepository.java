/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.taller_mecanico.repository;

import com.taller_mecanico.domain.DetalleOrden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taller_mecanico.domain.EstadoOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {

    List<DetalleOrden> findByOrdenIdOrden(Integer idOrden);
}
