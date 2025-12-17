package com.taller_mecanico.controller;

import com.taller_mecanico.domain.CarritoItem;
import com.taller_mecanico.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/cliente/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // ===============================
    // VER CARRITO
    // ===============================
 @GetMapping
public String verCarrito(Model model,
                         Authentication auth,
                         @RequestParam(required = false) String exito) {

    String username = auth.getName();

    List<CarritoItem> items = carritoService.listarCarrito(username);

    BigDecimal total = items.stream()
            .map(i -> i.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(i.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    model.addAttribute("items", items);
    model.addAttribute("total", total);

    if (exito != null) {
        model.addAttribute("compraExitosa", true);
    }

    return "cliente/carrito";
}


    // ===============================
    // AGREGAR AL CARRITO
    // ===============================
    @PostMapping("/agregar")
public String agregar(@RequestParam Integer idRepuesto,
                      @RequestParam Integer cantidad,
                      Authentication auth,
                      RedirectAttributes ra) {

    try {
        carritoService.agregarAlCarrito(auth.getName(), idRepuesto, cantidad);
        ra.addFlashAttribute("mensaje", "Producto agregado al carrito");
    } catch (IllegalStateException e) {
        ra.addFlashAttribute("errorStock", e.getMessage());
    }

    return "redirect:/cliente/repuestos";
}


    // ===============================
    // QUITAR ITEM
    // ===============================
    @PostMapping("/quitar/{idItem}")
    public String quitar(@PathVariable Integer idItem) {
        carritoService.quitarDelCarrito(idItem);
        return "redirect:/cliente/carrito";
    }

    // ===============================
    // VACIAR CARRITO
    // ===============================
    @PostMapping("/vaciar")
    public String vaciar(Authentication auth) {
        carritoService.vaciarCarrito(auth.getName());
        return "redirect:/cliente/carrito";
    }

    // ===============================
    // CONFIRMAR COMPRA (CHECKOUT)
    // ===============================
   @PostMapping("/confirmar")
public String confirmarCompra(Authentication auth, RedirectAttributes ra) {
    try {
        carritoService.confirmarCompra(auth.getName());
        ra.addFlashAttribute("compraExitosa", true);
    } catch (IllegalStateException e) {
        ra.addFlashAttribute("errorStock", e.getMessage());
    }
    return "redirect:/cliente/carrito";
}

}
