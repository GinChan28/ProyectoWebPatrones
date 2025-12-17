package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    Optional<Cliente> findByUsuarioUsername(String username);
    
    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.vehiculos v WHERE v.placa LIKE %:placa%")
    List<Cliente> findByVehiculoPlacaContaining(@Param("placa") String placa);
}
