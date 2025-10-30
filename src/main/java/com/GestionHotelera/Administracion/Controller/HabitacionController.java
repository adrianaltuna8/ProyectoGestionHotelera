package com.GestionHotelera.Administracion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.GestionHotelera.Administracion.Model.Habitacion;
import com.GestionHotelera.Administracion.Service.HabitacionService;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {
    @Autowired
    private HabitacionService habitacionService;

    // Listar
    @GetMapping
    public String listarHabitaciones(Model model) {
        List<Habitacion> habitaciones = habitacionService.obtenerTodas();
        model.addAttribute("habitaciones", habitaciones);
        return "habitaciones/listar";
    }

    // Nuevo
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        return "habitaciones/formulario";
    }

    // Guardar
    @PostMapping("/guardar")
    public String guardarHabitacion(@ModelAttribute Habitacion habitacion,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "habitaciones/formulario";
        }

        if (habitacionService.existeNumero(habitacion.getNumero()) && habitacion.getId() == null) {
            model.addAttribute("error", "El número de habitación ya existe");
            return "habitaciones/formulario";
        } else if (habitacionService.existeNumeroForUpdate(habitacion.getNumero(), habitacion.getId())) {
            model.addAttribute("error", "El número de habitación ya está en uso por otra");
            return "habitaciones/formulario";
        }

        habitacionService.guardar(habitacion);
        return "redirect:/habitaciones?exito";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Habitacion habitacion = habitacionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
        model.addAttribute("habitacion", habitacion);
        return "habitaciones/formulario";
    }
    // Eliminar
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarHabitacion(@PathVariable Long id) 
    {
        habitacionService.eliminar(id);
        return "redirect:/habitaciones?eliminado";
    }    
}
