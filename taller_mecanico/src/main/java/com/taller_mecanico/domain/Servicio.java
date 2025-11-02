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
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    private String nombre;
    private String descripcion;

    @Column(name = "precio_base")
    private Double precioBase;

    private Boolean activo;

}
