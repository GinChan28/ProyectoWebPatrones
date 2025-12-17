/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.taller_mecanico.repository;


import com.taller_mecanico.domain.OrdenCompra;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface OrdenCompraRepository
        extends JpaRepository<OrdenCompra, Integer> {

    Optional<OrdenCompra> findByClienteAndEstado(
            Cliente cliente,
            EstadoOrden estado
    );
    
    List<OrdenCompra> findByClienteAndEstadoNotOrderByFechaDesc(
        Cliente cliente,
        EstadoOrden estado
    );
}
