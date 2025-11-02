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

    private Boolean activo;

}
