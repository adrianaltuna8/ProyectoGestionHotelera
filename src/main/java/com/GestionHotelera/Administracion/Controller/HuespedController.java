package com.GestionHotelera.Administracion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.GestionHotelera.Administracion.Model.Huesped;
import com.GestionHotelera.Administracion.Service.HuespedService;

@Controller
@RequestMapping("/huespedes")
public class HuespedController {
@Autowired
    private HuespedService huespedService;

    // Listar todos los huéspedes
    @GetMapping
    public String listarHuespedes(Model model) {
        List<Huesped> huespedes = huespedService.obtenerTodosActivos();
        model.addAttribute("huespedes", huespedes);
        return "huespedes/listar";
    }

    // Mostrar formulario para nuevo huésped
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("huesped", new Huesped());
        return "huespedes/formulario";
    }

    // Mostrar formulario para editar huésped
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Huesped huesped = huespedService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));
        model.addAttribute("huesped", huesped);
        return "huespedes/formulario";
    }

    // Guardar (Crear o Actualizar) huésped
    @PostMapping("/guardar")
    public String guardarHuesped(@ModelAttribute Huesped huesped,
        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "huespedes/formulario";
        }

        // Validación de unicidad de la identificación (DNI/Cédula)
        if (huesped.getId() == null && huespedService.existeIdentificacion(huesped.getIdentificacion())) {
            model.addAttribute("error", "Ya existe un huésped con esta identificación.");
            return "huespedes/formulario";
        } else if (huesped.getId() != null && huespedService.existeIdentificacionForUpdate(huesped.getIdentificacion(), huesped.getId())) {
            model.addAttribute("error", "Esta identificación ya está en uso por otro huésped.");
            return "huespedes/formulario";
        }

        huespedService.guardar(huesped);
        return "redirect:/huespedes?exito";
    }

    // Eliminar (Soft Delete) huésped
    // Usamos el patrón de eliminar por ID que es capturado por el modal
    @PostMapping("/eliminar/{id}")
    public String eliminarHuesped(@PathVariable Long id) {
        huespedService.eliminar(id); // Implementado como soft delete en el servicio
        return "redirect:/huespedes?eliminado";
    }    
}
