package com.GestionHotelera.Administracion.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.GestionHotelera.Administracion.Model.ConsumoServicio;
import com.GestionHotelera.Administracion.Model.Reserva;
import com.GestionHotelera.Administracion.Service.ConsumoServicioService;
import com.GestionHotelera.Administracion.Service.ReservaService;
import com.GestionHotelera.Administracion.Service.ServicioService;

@Controller
@RequestMapping("/consumos")
public class ConsumoServicioController {
@Autowired
    private ConsumoServicioService consumoServicioService;

    @Autowired
    private ReservaService reservaService; // Usado para obtener la lista de Reservas Activas y el detalle de una Reserva

    @Autowired
    private ServicioService servicioService; // Usado para cargar el combo de servicios disponibles

    // Método auxiliar para cargar combos
    private void cargarDependencias(Model model) {
        // Asumiendo que servicioService.obtenerTodos() existe y funciona
        model.addAttribute("serviciosDisponibles", servicioService.obtenerTodos());
    }

    // =========================================================
    // VISTA 1: Listado de Reservas Activas (Puerta de entrada)
    // =========================================================

    /**
     * Muestra una lista de reservas activas (Pendiente, Confirmada, Check-In) 
     * que pueden ser cargadas con consumos.
     * URL: /consumos/listar
     */
    @GetMapping({"", "/listar"})
    public String listarReservasActivas(Model model) {
        // ✅ Usa el método correcto del ReservaService
        // Asumiendo que reservaService.obtenerReservasActivasParaFacturacion() existe
        model.addAttribute("reservas", reservaService.obtenerReservasActivasParaFacturacion());
        model.addAttribute("vistaActual", "Reservas Activas");
        
        return "consumos/listar"; 
    }


    // =========================================================
    // VISTA 2: Formulario de Gestión de Consumos (Registro)
    // =========================================================

    /**
     * Muestra la vista de gestión de consumos para una reserva específica.
     * Incluye el listado de consumos existentes y el formulario para añadir uno nuevo.
     * URL: /consumos/formulario/{reservaId}
     */
    @GetMapping("/formulario/{reservaId}")
    public String gestionarConsumos(@PathVariable Long reservaId, Model model, RedirectAttributes redirect) {
        
        return reservaService.obtenerPorId(reservaId).map(reserva -> {
            
            String estadoReserva = reserva.getEstadoReserva();
            
            // Bloquea la carga si la reserva está terminada o cancelada
            if ("Check-Out".equals(estadoReserva) || "Cancelada".equals(estadoReserva)) {
                 redirect.addFlashAttribute("errorMessage", "La Reserva #" + reservaId + " está cerrada (" + estadoReserva + "). No se permiten nuevos cargos.");
                 return "redirect:/consumos/listar"; 
            }

            // Datos para la vista
            model.addAttribute("reserva", reserva);
            model.addAttribute("consumos", consumoServicioService.obtenerConsumosPorReserva(reservaId));
            
            // Objeto ConsumoServicio vacío para el formulario (con la reserva pre-asignada)
            ConsumoServicio nuevoConsumo = new ConsumoServicio();
            nuevoConsumo.setReserva(reserva);
            
            model.addAttribute("nuevoConsumo", nuevoConsumo);
            cargarDependencias(model);
            
            return "consumos/formulario"; 
            
        }).orElseGet(() -> {
            redirect.addFlashAttribute("errorMessage", "Reserva no encontrada para gestionar consumos.");
            return "redirect:/consumos/listar";
        });
    }

    // =========================================================
    // ACCIÓN: Registrar un Nuevo Consumo
    // =========================================================

    /**
     * Procesa el envío del formulario para registrar un nuevo consumo.
     * Redirige de vuelta a la vista de gestión de esa reserva.
     */
    @PostMapping("/guardar")
    public String guardarConsumo(@ModelAttribute("nuevoConsumo") ConsumoServicio consumo, RedirectAttributes redirect) {
        
        Long reservaId = consumo.getReserva().getId();
        Long servicioId = consumo.getServicio().getId();
        int cantidad = consumo.getCantidad() != null ? consumo.getCantidad() : 0;
        ConsumoServicio consumoRegistrado; 

        try {
            // Llama al servicio, que maneja la lógica de negocio (validaciones, precio, persistencia)
            consumoRegistrado = consumoServicioService.registrarConsumo(reservaId, servicioId, cantidad);
            
            // Formatea el subtotal del objeto registrado para el mensaje de éxito
            String subtotalFormateado = String.format("%.2f", consumoRegistrado.getSubtotal());
            
            redirect.addFlashAttribute("successMessage", 
                "Consumo de **" + consumoRegistrado.getServicio().getNombre() + 
                "** registrado con éxito. Subtotal: S/ " + subtotalFormateado);
            
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", "Error al registrar consumo: " + e.getMessage());
        }
        
        // Redirige de vuelta a la misma vista de gestión (formulario)
        return "redirect:/consumos/formulario/" + reservaId;
    }
}
