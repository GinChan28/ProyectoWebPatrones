/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.taller_mecanico.controller;

import com.taller_mecanico.service.OrdenService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Controller
@RequestMapping("/cliente/ordenes")
public class ClienteOrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public String historial(Authentication auth, Model model) {

        String username = auth.getName();

        model.addAttribute(
            "ordenes",
            ordenService.historialCliente(username)
        );

        return "cliente/historial-ordenes";
    }
}
