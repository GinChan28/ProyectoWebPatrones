/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 *
 * 
 */
@Data
@Entity
@Table(name = "archivo")
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Integer idArchivo;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita; // puede ser null

    @ManyToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo; // puede ser null

    @Enumerated(EnumType.STRING)
    private Tipo tipo; // FOTO_VEHICULO, EVIDENCIA_SERVICIO, OTRO

    @Column(name = "url_publica")
    private String urlPublica;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    public enum Tipo {
        FOTO_VEHICULO, EVIDENCIA_SERVICIO, OTRO
    }

}
