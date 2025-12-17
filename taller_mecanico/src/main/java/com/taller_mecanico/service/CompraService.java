package com.taller_mecanico.service;

import com.taller_mecanico.domain.DetalleOrden;
import com.taller_mecanico.domain.EstadoOrden;
import com.taller_mecanico.domain.OrdenCompra;
import com.taller_mecanico.repository.OrdenCompraRepository;
import com.taller_mecanico.repository.RepuestoRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraService {

    @Autowired
    private OrdenCompraRepository ordenRepo;

    @Autowired
    private RepuestoRepository repuestoRepo;

    @Transactional
    public void confirmarOrden(Integer idOrden) {

        OrdenCompra orden = ordenRepo.findById(idOrden)
                .orElseThrow(() ->
                        new IllegalStateException("Orden no encontrada")
                );

        for (DetalleOrden d : orden.getDetalles()) {

            int actualizado = repuestoRepo.descontarStock(
                    d.getRepuesto().getIdRepuesto(),
                    d.getCantidad()
            );

            if (actualizado == 0) {
                throw new IllegalStateException(
                        "Stock insuficiente para " + d.getRepuesto().getNombre()
                );
            }
        }

        orden.setEstado(EstadoOrden.PAGADA);
        orden.setFecha(LocalDateTime.now());

        ordenRepo.save(orden);
    }
}
