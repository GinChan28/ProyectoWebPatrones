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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String username;
    private String password;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;

    @Column(name = "ruta_imagen")
    private String rutaImagen;

    private Boolean activo;
}
