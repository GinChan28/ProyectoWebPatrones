/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author megan
 */
@Controller
public class IndexController {

    @GetMapping({"/", "/index"})
    public String index() {
        // lleva directo a la lista de citas
        return /*"/index"*/
                
                "redirect:/cita/lista";
    }

}
