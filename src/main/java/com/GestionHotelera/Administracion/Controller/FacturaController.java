package com.GestionHotelera.Administracion.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.GestionHotelera.Administracion.Model.Factura;
import com.GestionHotelera.Administracion.Service.FacturaService;
@Controller
@RequestMapping("/facturas")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    // =====================================================
    // LISTAR TODAS LAS FACTURAS
    // =====================================================
    @GetMapping
    public String listarFacturas(Model model) {
        model.addAttribute("facturas", facturaService.obtenerTodas());
        model.addAttribute("vistaActual", "Listado de Facturas");
        return "facturas/listar";
    }

    // =====================================================
    // DETALLE DE UNA FACTURA
    // =====================================================
    @GetMapping("/{id}")
    public String verDetalleFactura(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Factura factura = facturaService.obtenerPorId(id)
                .orElse(null);

        if (factura == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Factura no encontrada.");
            return "redirect:/facturas";
        }

        model.addAttribute("factura", factura);
        model.addAttribute("vistaActual", "Detalle de Factura");
        return "facturas/detalle";
    }

    // =====================================================
    // MARCAR COMO PAGADA
    // =====================================================
    @GetMapping("/pagar/{id}")
    public String marcarComoPagada(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            facturaService.marcarComoPagada(id);
            redirectAttrs.addFlashAttribute("successMessage", "Factura marcada como pagada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo marcar la factura como pagada.");
        }
        return "redirect:/facturas";
    }
}
