package com.taller_mecanico.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "repuesto")
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repuesto")
    private Integer idRepuesto;

    private String nombre;
    private String sku;

    private Integer existencias;

    @Column(name = "costo_unitario")
    private Double costoUnitario;

    @Column(nullable = false)
    private Boolean activo = true; 
}
