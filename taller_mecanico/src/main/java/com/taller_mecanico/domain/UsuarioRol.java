/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author megan
 */
@Data
@Entity
@Table(name = "usuario_rol")
@IdClass(UsuarioRol.UsuarioRolPK.class)
public class UsuarioRol {


    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Id
    @Column(name = "id_rol")
    private Integer idRol;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_rol", insertable = false, updatable=false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_rol", insertable = false, updatable=false)
    private Rol rol;

    @Data
    public static class UsuarioRolPK implements Serializable {

        private Integer idUsuario;
        private Integer idRol;
    }
}
