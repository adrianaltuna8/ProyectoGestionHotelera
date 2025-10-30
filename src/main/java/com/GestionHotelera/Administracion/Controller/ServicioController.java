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

import com.GestionHotelera.Administracion.Model.Servicio;
import com.GestionHotelera.Administracion.Service.ServicioService;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;
    // Listar solo servicios activos
    @GetMapping
    public String listarServicios(Model model) {
        List<Servicio> servicios = servicioService.obtenerTodosActivos(); // Usamos solo activos
        model.addAttribute("servicios", servicios);
        return "servicios/listar";
    }

    // Nuevo
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/formulario";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Servicio servicio = servicioService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        model.addAttribute("servicio", servicio);
        return "servicios/formulario";
    }

    // Guardar (Crear o Actualizar)
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute Servicio servicio,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "servicios/formulario";
        }
        
        servicioService.guardar(servicio);
        return "redirect:/servicios?exito";
    }

    // Eliminar (Soft Delete)
    @PostMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        servicioService.eliminar(id);
        return "redirect:/servicios?eliminado";
    }    
}
