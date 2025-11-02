/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author megan
 */
@Data
@Entity
@Table(name = "cita_historial_estado")
public class CitaHistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private Cita.Estado estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo")
    private Cita.Estado estadoNuevo;

    @Column(name = "cambiado_en")
    private LocalDateTime cambiadoEn;
}
