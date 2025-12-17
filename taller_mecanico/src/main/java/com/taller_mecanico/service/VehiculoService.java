package com.taller_mecanico.service;

import com.taller_mecanico.domain.Cliente;
import com.taller_mecanico.domain.Vehiculo;
import com.taller_mecanico.repository.ClienteRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository,
                           ClienteRepository clienteRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Vehiculo> porCliente(Integer idCliente) {
        return vehiculoRepository.findByClienteIdCliente(idCliente);
    }

    // Buscar vehículos del usuario logueado
    public List<Vehiculo> listarPorCliente(String username) {

        Cliente cliente = clienteRepository.findByUsuarioUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Cliente no encontrado para el usuario: " + username
                        )
                );

        return vehiculoRepository.findByClienteIdCliente(cliente.getIdCliente());
    }

    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }
}
