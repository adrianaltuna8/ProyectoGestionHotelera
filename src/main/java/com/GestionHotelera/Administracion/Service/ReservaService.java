package com.GestionHotelera.Administracion.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Factura;
import com.GestionHotelera.Administracion.Model.Habitacion;
import com.GestionHotelera.Administracion.Model.Reserva;
import com.GestionHotelera.Administracion.Repository.ReservaRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservaService {
@Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private FacturaService facturaService;
    @Autowired
    private HabitacionService habitacionService; // Para obtener el precio

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }
public List<Reserva> obtenerReservasActivasParaFacturacion() {
        List<String> estadosActivos = Arrays.asList("Pendiente", "Confirmada", "Check-In");
        return reservaRepository.findByEstadoReservaInOrderByFechaEntradaAsc(estadosActivos);
    }    

    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }
    
public Reserva guardar(Reserva reserva) {
        
        // 1. Obtener ID a excluir (Si es nueva, usamos -1L, si es edición, usamos su ID)
        Long idExcluir = reserva.getId() == null ? -1L : reserva.getId();

        if (reserva.getFechaEntrada() != null && reserva.getFechaSalida() != null && reserva.getHabitacion() != null) {
            
            // VALIDACIÓN 1: Fechas válidas
            if (!reserva.getFechaEntrada().isBefore(reserva.getFechaSalida())) {
                throw new RuntimeException("La fecha de entrada debe ser estrictamente anterior a la fecha de salida.");
            }
            
            // VALIDACIÓN 2: Conflicto de habitación y fechas
            List<Reserva> conflictos = reservaRepository.findConflictosDeReserva(
                reserva.getHabitacion().getId(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                idExcluir
            );
            
            if (!conflictos.isEmpty()) {
                throw new RuntimeException("Conflicto: La habitación ya tiene reservas activas en las fechas seleccionadas.");
            }

            // 3. Calcular Precio Total (Lógica existente)
            long dias = ChronoUnit.DAYS.between(reserva.getFechaEntrada(), reserva.getFechaSalida());
            Habitacion habitacion = habitacionService.obtenerPorId(reserva.getHabitacion().getId())
                                                 .orElseThrow(() -> new RuntimeException("Habitación no válida."));
            
            double precioNoche = habitacion.getPrecioPorNoche();
            reserva.setPrecioTotal(dias * precioNoche);
        }
        
        // 4. Estado inicial (Lógica existente)
        if (reserva.getId() == null && reserva.getEstadoReserva() == null) {
            reserva.setEstadoReserva("Pendiente");
        }

        return reservaRepository.save(reserva);
    }

    // =========================================================
    // MÉTODOS DE FILTRADO (Vistas Segmentadas)
    // =========================================================

    public List<Reserva> obtenerLlegadasDeHoy() {
        List<String> estadosActivos = Arrays.asList("Pendiente", "Confirmada");
        return reservaRepository.findByFechaEntradaAndEstadoReservaInOrderByFechaEntradaAsc(
            LocalDate.now(), 
            estadosActivos
        );
    }

    public List<Reserva> obtenerReservasAlojadas() {
        return reservaRepository.findByEstadoReservaOrderByFechaSalidaAsc("Check-In");
    }

    public List<Reserva> obtenerSalidasDeHoy() {
        return reservaRepository.findByFechaSalidaAndEstadoReservaOrderByFechaSalidaAsc(
            LocalDate.now(), 
            "Check-In"
        );
    }
    
    public List<Reserva> obtenerProximasLlegadas() {
        List<String> estadosActivos = Arrays.asList("Pendiente", "Confirmada");
        return reservaRepository.findByFechaEntradaAfterAndEstadoReservaInOrderByFechaEntradaAsc(
            LocalDate.now(), 
            estadosActivos
        );
    }
    
    public List<Reserva> obtenerHistorial() {
        List<String> estadosFinalizados = Arrays.asList("Check-Out", "Cancelada");
        return reservaRepository.findByEstadoReservaInOrderByFechaSalidaDesc(
            estadosFinalizados
        );
    }

    // =========================================================
    // TRANSICIONES DE ESTADO (Acciones Rápidas)
    // =========================================================
public void realizarCheckIn(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        
        if ("Pendiente".equals(reserva.getEstadoReserva()) || "Confirmada".equals(reserva.getEstadoReserva())) {
            reserva.setEstadoReserva("Check-In");
            reservaRepository.save(reserva);
            
            // 💡 Sincronización: Habitación pasa a Ocupada
            habitacionService.marcarComoOcupada(reserva.getHabitacion().getId()); 
        } else {
            throw new IllegalStateException("No se puede hacer Check-In en el estado actual: " + reserva.getEstadoReserva());
        }
    }


    public void cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        
        if (!"Check-In".equals(reserva.getEstadoReserva()) && !"Check-Out".equals(reserva.getEstadoReserva())) {
            reserva.setEstadoReserva("Cancelada");
            reservaRepository.save(reserva);
            
            // 💡 Sincronización: Habitación vuelve a Disponible (si no está en mantenimiento)
            habitacionService.marcarComoDisponible(reserva.getHabitacion().getId());
        } else {
             throw new IllegalStateException("No se puede cancelar una reserva que ya está en " + reserva.getEstadoReserva());
        }
    }

    @Transactional
    public void marcarReservaComoConfirmada(Reserva reserva) {
    if ("Pendiente".equals(reserva.getEstadoReserva())) {
        reserva.setEstadoReserva("Confirmada");
        reservaRepository.save(reserva);
    }
    // Si ya está en Confirmada o Check-In, no hace nada (lo que es seguro)
}
//Analizar bien en Factura y tomar precauciones
@Transactional // Garantiza que la Factura y la Reserva se guarden juntas
public void realizarCheckOut(Long id) {
    Reserva reserva = reservaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    
    if ("Check-In".equals(reserva.getEstadoReserva())) {
        
        // PASO 1: Generar la Factura (consolida consumos y calcula totales)
        // La FacturaService se encarga de que los consumos queden marcados como facturados
        Factura factura = facturaService.generarFacturaFinal(reserva);
        
        // 2. Cambiar estado de la Reserva
        reserva.setEstadoReserva("Check-Out");
        reservaRepository.save(reserva);

        // 3. Sincronización: Habitación pasa a En Limpieza
        habitacionService.marcarComoNecesitaLimpieza(reserva.getHabitacion().getId());
        
    } else {
        throw new IllegalStateException("No se puede hacer Check-Out en el estado actual: " + reserva.getEstadoReserva());
    }
}
}
