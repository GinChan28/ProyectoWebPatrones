/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.repository;

import com.taller_mecanico.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author megan
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByRol(String rol);// "CLIENTE", "MECANICO", "ADMIN"

}
