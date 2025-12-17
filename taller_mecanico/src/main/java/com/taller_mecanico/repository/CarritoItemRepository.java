package com.taller_mecanico.repository;

import com.taller_mecanico.domain.CarritoItem;
import com.taller_mecanico.domain.OrdenCompra;
import com.taller_mecanico.domain.Repuesto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {

    // Items de una orden (carrito)
    List<CarritoItem> findByOrden(OrdenCompra orden);

    // Buscar repuesto dentro del carrito
    CarritoItem findByOrdenAndRepuesto(OrdenCompra orden, Repuesto repuesto);

    // Vaciar carrito
    void deleteByOrden(OrdenCompra orden);
}
