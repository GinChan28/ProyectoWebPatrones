/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.UsuarioRol;
import com.taller_mecanico.domain.UsuarioRol.UsuarioRolPK;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolPK> {

    List<UsuarioRol> findByUsuario_IdUsuario(Integer idUsuario);

    List<UsuarioRol> findByIdRol(Integer idRol);

    /*public void deleteByIdUsuario(Integer idUsuario);*/
}
