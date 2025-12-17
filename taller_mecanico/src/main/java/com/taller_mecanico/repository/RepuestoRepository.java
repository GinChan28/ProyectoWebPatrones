package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Repuesto;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RepuestoRepository extends JpaRepository<Repuesto, Integer> {

    @Modifying
    @Transactional
    @Query("""
        UPDATE Repuesto r
        SET r.existencias = r.existencias - :cantidad
        WHERE r.idRepuesto = :id
          AND r.existencias >= :cantidad
    """)
    int descontarStock(
        @Param("id") Integer id,
        @Param("cantidad") Integer cantidad
    );

    List<Repuesto> findByActivoTrue();

    List<Repuesto> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);

    Optional<Repuesto> findBySku(String sku);

    Repuesto findByNombreOrSku(String nombre, String sku);
}
