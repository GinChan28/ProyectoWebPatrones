package com.taller_mecanico.service;

import com.taller_mecanico.domain.CarritoItem;
import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.DetalleOrden;
import com.taller_mecanico.domain.EstadoOrden;
import com.taller_mecanico.domain.OrdenCompra;
import com.taller_mecanico.domain.Repuesto;
import com.taller_mecanico.repository.CarritoItemRepository;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.DetalleOrdenRepository;
import com.taller_mecanico.repository.OrdenCompraRepository;
import com.taller_mecanico.repository.RepuestoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    @Autowired
    private CarritoItemRepository carritoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private RepuestoRepository repuestoRepo;

    @Autowired
    private OrdenCompraRepository ordenRepo;

    @Autowired
    private DetalleOrdenRepository detalleRepo;

    // ===============================
    // REPUESTOS DISPONIBLES
    // ===============================
    public List<Repuesto> listarRepuestosDisponibles() {
        return repuestoRepo.findByActivoTrue();
    }

    // ===============================
    // OBTENER O CREAR ORDEN CARRITO
    // ===============================
    private OrdenCompra obtenerOrdenCarrito(Cliente cliente) {

        return ordenRepo
                .findByClienteAndEstado(cliente, EstadoOrden.CARRITO)
                .orElseGet(() -> {
                    OrdenCompra orden = new OrdenCompra();
                    orden.setCliente(cliente);
                    orden.setEstado(EstadoOrden.CARRITO);
                    orden.setFecha(LocalDateTime.now());
                    return ordenRepo.save(orden);
                });
    }

    // ===============================
    // LISTAR CARRITO
    // ===============================
   public List<CarritoItem> listarCarrito(String username) {

    Cliente cliente = clienteRepo
            .findByUsuarioUsername(username)
            .orElseThrow();

    Optional<OrdenCompra> ordenOpt =
            ordenRepo.findByClienteAndEstado(cliente, EstadoOrden.CARRITO);

    if (ordenOpt.isEmpty()) {
        return List.of(); 
    }

    return carritoRepo.findByOrden(ordenOpt.get());
}

 


    // ===============================
    // AGREGAR AL CARRITO
    // ===============================
@Transactional
public void agregarAlCarrito(String username, Integer idRepuesto, Integer cantidad) {

    Cliente cliente = clienteRepo.findByUsuarioUsername(username)
            .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

    OrdenCompra orden = obtenerOrdenCarrito(cliente);

    Repuesto repuesto = repuestoRepo.findById(idRepuesto)
            .orElseThrow(() -> new IllegalStateException("Repuesto no encontrado"));

    CarritoItem itemExistente = carritoRepo.findByOrdenAndRepuesto(orden, repuesto);

    int cantidadActual = (itemExistente != null) ? itemExistente.getCantidad() : 0;
    int cantidadTotal = cantidadActual + cantidad;
    if (cantidadTotal > repuesto.getExistencias()) {
        throw new IllegalStateException(
                "Stock insuficiente. Disponible: " + repuesto.getExistencias()
        );
    }

    if (itemExistente != null) {
        itemExistente.setCantidad(cantidadTotal);
        carritoRepo.save(itemExistente);
    } else {
        CarritoItem nuevoItem = new CarritoItem();
        nuevoItem.setOrden(orden);
        nuevoItem.setRepuesto(repuesto);
        nuevoItem.setCantidad(cantidad);
        nuevoItem.setPrecioUnitario(
                BigDecimal.valueOf(repuesto.getCostoUnitario())
        );
        carritoRepo.save(nuevoItem);
    }
}



    // ===============================
    // QUITAR ITEM
    // ===============================
    @Transactional
    public void quitarDelCarrito(Integer idItem) {

        CarritoItem item = carritoRepo
                .findById(idItem)
                .orElseThrow();

        if (item.getCantidad() > 1) {
            item.setCantidad(item.getCantidad() - 1);
            carritoRepo.save(item);
        } else {
            carritoRepo.delete(item);
        }
    }

    // ===============================
    // VACIAR CARRITO
    // ===============================
    @Transactional
    public void vaciarCarrito(String username) {

        Cliente cliente = clienteRepo
                .findByUsuarioUsername(username)
                .orElseThrow();

        OrdenCompra orden = obtenerOrdenCarrito(cliente);

        carritoRepo.deleteByOrden(orden);
    }

    // ===============================
    // CONFIRMAR COMPRA
    // ===============================
    @Transactional
public OrdenCompra confirmarCompra(String username) {

    Cliente cliente = clienteRepo.findByUsuarioUsername(username)
            .orElseThrow();

    OrdenCompra orden = ordenRepo
            .findByClienteAndEstado(cliente, EstadoOrden.CARRITO)
            .orElseThrow();

    List<CarritoItem> items = carritoRepo.findByOrden(orden);

    if (items.isEmpty()) {
        throw new IllegalStateException("Carrito vacío");
    }

    for (CarritoItem item : items) {

        Repuesto r = item.getRepuesto();

        if (r.getExistencias() < item.getCantidad()) {
            throw new IllegalStateException(
                "Stock insuficiente para " + r.getNombre()
            );
        }

        r.setExistencias(r.getExistencias() - item.getCantidad());
        repuestoRepo.save(r);

        DetalleOrden d = new DetalleOrden();
        d.setOrden(orden);
        d.setRepuesto(r);
        d.setCantidad(item.getCantidad());
        d.setPrecioUnitario(item.getPrecioUnitario().doubleValue());

        detalleRepo.save(d);
    }

    orden.setEstado(EstadoOrden.PAGADA);
    ordenRepo.save(orden);

    carritoRepo.deleteByOrden(orden);

    return orden;
}

}