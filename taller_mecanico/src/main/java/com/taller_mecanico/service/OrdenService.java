/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.taller_mecanico.service;

import com.taller_mecanico.domain.DetalleOrden;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.EstadoOrden;
import com.taller_mecanico.domain.OrdenCompra;
import com.taller_mecanico.repository.OrdenCompraRepository;
import com.taller_mecanico.repository.RepuestoRepository;
import com.taller_mecanico.repository.ClienteRepository;



import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {

    @Autowired
    private OrdenCompraRepository ordenRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    public List<OrdenCompra> historialCliente(String username) {

        Cliente cliente = clienteRepo
                .findByUsuarioUsername(username)
                .orElseThrow();

        return ordenRepo.findByClienteAndEstadoNotOrderByFechaDesc(
                cliente,
                EstadoOrden.CARRITO
        );
    }
}
