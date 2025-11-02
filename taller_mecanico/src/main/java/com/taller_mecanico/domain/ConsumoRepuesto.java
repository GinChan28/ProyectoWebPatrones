/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 *
 * @author megan
 */
@Data
@Entity
@Table(name = "consumo_repuesto",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_cita", "id_repuesto"}))
public class ConsumoRepuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consumo")
    private Integer idConsumo;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_repuesto")
    private Repuesto repuesto;

    private Integer cantidad; // > 0 (validarlo en service)
}
