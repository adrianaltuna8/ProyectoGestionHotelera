package com.GestionHotelera.Administracion.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.GestionHotelera.Administracion.Model.Habitacion;
import com.GestionHotelera.Administracion.Model.Huesped;
import com.GestionHotelera.Administracion.Model.Reserva;
import com.GestionHotelera.Administracion.Service.HabitacionService;
import com.GestionHotelera.Administracion.Service.HuespedService;
import com.GestionHotelera.Administracion.Service.ReservaService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {
@Autowired
    private ReservaService reservaService;

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private HuespedService huespedService;

    private void cargarDependencias(Model model) {
        List<Habitacion> habitaciones = habitacionService.obtenerDisponiblesParaReserva();
        List<Huesped> huespedes = huespedService.obtenerTodosActivos(); // O solo obtenerTodas() si no hay lógica de activo/inactivo

        // ¡ATENCIÓN al nombre del atributo 'habitaciones' y 'huespedes'!
        model.addAttribute("habitaciones", habitaciones); 
        model.addAttribute("huespedes", huespedes);
    }
    
    // =========================================================
    // VISTAS SEGMENTADAS (Listado) - (Se mantienen iguales)
    // =========================================================

    // Muestra por defecto las llegadas de hoy
    @GetMapping({"", "/"})
    public String dashboardReservas(Model model) {
        List<Reserva> llegadas = reservaService.obtenerLlegadasDeHoy();
        model.addAttribute("reservas", llegadas);
        model.addAttribute("vistaActual", "Llegadas de Hoy");
        return "reservas/listar";
    }
    

    @GetMapping("/alojadas")
    public String mostrarAlojadas(Model model) {
        List<Reserva> alojadas = reservaService.obtenerReservasAlojadas();
        model.addAttribute("reservas", alojadas);
        model.addAttribute("vistaActual", "Huéspedes Alojados");
        return "reservas/listar";
    }

    @GetMapping("/salidas")
    public String mostrarSalidas(Model model) {
        List<Reserva> salidas = reservaService.obtenerSalidasDeHoy();
        model.addAttribute("reservas", salidas);
        model.addAttribute("vistaActual", "Salidas de Hoy");
        return "reservas/listar";
    }
    
    @GetMapping("/proximas")
    public String mostrarProximas(Model model) {
        List<Reserva> proximas = reservaService.obtenerProximasLlegadas();
        model.addAttribute("reservas", proximas);
        model.addAttribute("vistaActual", "Próximas Llegadas");
        return "reservas/listar";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorial(Model model) {
        List<Reserva> historial = reservaService.obtenerHistorial();
        model.addAttribute("reservas", historial);
        model.addAttribute("vistaActual", "Historial");
        return "reservas/listar";
    }

    // =========================================================
    // TRANSICIONES DE ESTADO (Acciones Rápidas) - (Se mantienen iguales)
    // =========================================================
    
    private String redireccionarConMensaje(Long id, String action, RedirectAttributes redirect) {
        // ... (Tu lógica de redireccionamiento se mantiene) ...
        try {
            if ("checkin".equals(action)) {
                reservaService.realizarCheckIn(id);
                redirect.addFlashAttribute("successMessage", "Check-In realizado con éxito.");
            } else if ("checkout".equals(action)) {
                reservaService.realizarCheckOut(id);
                redirect.addFlashAttribute("successMessage", "Check-Out realizado con éxito.");
            } else if ("cancelar".equals(action)) {
                reservaService.cancelar(id);
                redirect.addFlashAttribute("successMessage", "Reserva cancelada con éxito.");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Error al realizar la acción: " + e.getMessage());
        }
        return "redirect:/reservas";
    }

    @GetMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id, RedirectAttributes redirect) {
        return redireccionarConMensaje(id, "checkin", redirect);
    }

    @GetMapping("/checkout/{id}")
    public String checkOut(@PathVariable Long id, RedirectAttributes redirect) {
        return redireccionarConMensaje(id, "checkout", redirect);
    }
    
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        return redireccionarConMensaje(id, "cancelar", redirect);
    }
    
    // =========================================================
    // FORMULARIO (Creación y Edición) - ¡Corrección de combos!
    // =========================================================
    
    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("reserva", new Reserva());
        // ¡CAMBIO 3: Usar el método auxiliar!
        cargarDependencias(model); 
        return "reservas/formulario";
    }
    
@GetMapping("/formulario/{id}")
    public String editarFormulario(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Reserva> reserva = reservaService.obtenerPorId(id);
        if (reserva.isPresent()) {
            model.addAttribute("reserva", reserva.get());
            cargarDependencias(model); 
            
            // Lógica adicional para edición: Si la habitación de la reserva actual 
            // no estaba en la lista de "disponibles", debemos asegurarnos de añadirla 
            // al Model para que aparezca seleccionada en el combo.
            if (!model.asMap().containsKey("habitaciones")) {
                cargarDependencias(model); // Recarga la lista filtrada
            }
            
            // Añade la habitación actual (que puede estar "Ocupada" o en "Limpieza")
            // a la lista si no está ya presente (para edición).
            Habitacion habitacionActual = reserva.get().getHabitacion();
            List<Habitacion> habitacionesEnModel = (List<Habitacion>) model.asMap().get("habitaciones");
            
            if (habitacionActual != null && !habitacionesEnModel.contains(habitacionActual)) {
                habitacionesEnModel.add(habitacionActual);
            }
            
            return "reservas/formulario";
        }
        redirect.addFlashAttribute("errorMessage", "Reserva no encontrada.");
        return "redirect:/reservas";
    }

    @PostMapping("/guardar")
    public String guardarReserva(@ModelAttribute Reserva reserva, BindingResult result, Model model, RedirectAttributes redirect) {
        // validacion
        if (result.hasErrors()) {
            cargarDependencias(model); // Recargar datos si hay errores para que los combos no estén vacíos
            return "reservas/formulario";
        }
        try {
            reservaService.guardar(reserva);
            redirect.addFlashAttribute("successMessage", "Reserva guardada con éxito.");
        } catch (RuntimeException e) {
             redirect.addFlashAttribute("errorMessage", "Error al guardar la reserva: " + e.getMessage());
        }
        return "redirect:/reservas";
    }
}
