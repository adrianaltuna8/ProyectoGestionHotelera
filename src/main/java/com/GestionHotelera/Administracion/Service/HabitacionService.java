package com.GestionHotelera.Administracion.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Habitacion;
import com.GestionHotelera.Administracion.Repository.HabitacionRepository;

@Service
public class HabitacionService {
    @Autowired
    private HabitacionRepository habitacionRepository;

    public List<Habitacion> obtenerTodas() {
        return habitacionRepository.findAll();
    }

    public List<Habitacion> obtenerDisponiblesParaReserva() {
        // Estados que NO deberían aparecer en la lista de selección para una NUEVA reserva
        List<String> estadosExcluidos = Arrays.asList("Ocupada", "En Limpieza", "Mantenimiento");
        return habitacionRepository.findByEstadoNotIn(estadosExcluidos);
    }

    public Optional<Habitacion> obtenerPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    public Habitacion guardar(Habitacion habitacion) {
        if (habitacion.getFlgEstado() == null) {
            habitacion.setFlgEstado(0);
        }
        return habitacionRepository.save(habitacion);
    }

    public void eliminar(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        habitacion.setFlgEstado(0);
        habitacionRepository.save(habitacion); // Soft delete
    }

    public boolean existeNumero(String numero) {
        return habitacionRepository.existsByNumero(numero);
    }

    public boolean existeNumeroForUpdate(String numero, Long id) {
        return habitacionRepository.existsByNumeroAndIdNot(numero, id);
    }    
// =========================================================
    // NUEVOS MÉTODOS PARA SINCRONIZACIÓN CON RESERVA
    // =========================================================

    /**
     * Actualiza el estado de la habitación de forma segura.
     * Usado por ReservaService durante el Check-in/out.
     */
    private void actualizarEstadoInterno(Long habitacionId, String nuevoEstado) {
        Habitacion habitacion = obtenerPorId(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada para sincronización."));
        
        // El estado 'Mantenimiento' es generalmente manual, por eso solo afectamos Disponible/Ocupada/Limpieza
        if ("Mantenimiento".equals(habitacion.getEstado())) {
             // Opcional: Lanzar una excepción si se intenta cambiar el estado de una habitación en mantenimiento
             // throw new IllegalStateException("No se puede cambiar el estado de una habitación en Mantenimiento.");
        }
        
        habitacion.setEstado(nuevoEstado);
        // Usamos el mismo método guardar() para persistir el cambio
        habitacionRepository.save(habitacion); 
    }
    
    // Métodos de conveniencia para el ReservaService
    
    public void marcarComoOcupada(Long habitacionId) {
        actualizarEstadoInterno(habitacionId, "Ocupada");
    }

    public void marcarComoNecesitaLimpieza(Long habitacionId) {
        actualizarEstadoInterno(habitacionId, "En Limpieza");
    }

    public void marcarComoDisponible(Long habitacionId) {
        // Solo la marcamos como disponible si NO está en Mantenimiento
        Habitacion habitacion = obtenerPorId(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
                
        if (!"Mantenimiento".equals(habitacion.getEstado())) {
            actualizarEstadoInterno(habitacionId, "Disponible");
        }
    }    
}
