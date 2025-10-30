package com.GestionHotelera.Administracion.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.ConsumoServicio;
import com.GestionHotelera.Administracion.Model.Reserva;
import com.GestionHotelera.Administracion.Model.Servicio;
import com.GestionHotelera.Administracion.Repository.ConsumoServicioRepository;

import jakarta.transaction.Transactional;

@Service
public class ConsumoServicioService {
@Autowired
    private ConsumoServicioRepository consumoServicioRepository;
    
    // Inyección de la capa de negocio de Reserva para obtener datos y validar estado
    @Autowired
    private ReservaService reservaService; 
    
    // Inyección de la capa de negocio de Servicio para obtener precios y validar existencia
    @Autowired
    private ServicioService servicioService; 

    // Obtener todos los consumos de una reserva específica
    public List<ConsumoServicio> obtenerConsumosPorReserva(Long reservaId) {
        // Usa ReservaService para obtener la entidad, validando su existencia
        Reserva reserva = reservaService.obtenerPorId(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));
        
        return consumoServicioRepository.findByReservaOrderByFechaConsumoDesc(reserva);
    }
    
    // Obtener solo los consumos que aún no han sido incluidos en una factura final
    public List<ConsumoServicio> obtenerPendientesPorReserva(Reserva reserva) {
        return consumoServicioRepository.findByReservaAndFacturado(reserva, false);
    }

    /**
     * Registra un nuevo consumo asociado a una reserva, calculando el precio unitario actual.
     * * @param reservaId ID de la reserva a la que se cargará el consumo.
     * @param servicioId ID del servicio consumido.
     * @param cantidad Cantidad consumida.
     * @return El objeto ConsumoServicio creado y guardado.
     */
    @Transactional
    public ConsumoServicio registrarConsumo(Long reservaId, Long servicioId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        
        Reserva reserva = reservaService.obtenerPorId(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no válida."));
        
        Servicio servicio = servicioService.obtenerPorId(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no válido."));
        
        // 🚨 Validación de Estado: Solo se permite registrar consumos si la reserva está activa
        String estado = reserva.getEstadoReserva();
        if (!"Confirmada".equals(estado) && !"Check-In".equals(estado)) {
            throw new IllegalStateException("No se puede añadir consumos a una reserva en estado: " + estado);
        }

        ConsumoServicio consumo = new ConsumoServicio();
        consumo.setReserva(reserva);
        consumo.setServicio(servicio);
        consumo.setCantidad(cantidad);
        // Capturar el precio actual del servicio (Crucial para la precisión financiera)
        consumo.setPrecioUnitario(servicio.getPrecio()); 
        consumo.setFechaConsumo(LocalDate.now());
        consumo.setFacturado(false); 

        return consumoServicioRepository.save(consumo);
    } 
    
    // =========================================================
    // LÓGICA DE FACTURACIÓN (Llamado por FacturacionService)
    // =========================================================
    
    /**
     * Marca una lista de consumos como facturados después de la liquidación final.
     * Esto previene la doble facturación.
     */
    @Transactional
    public void marcarConsumosComoFacturados(List<ConsumoServicio> consumos) {
        if (consumos == null || consumos.isEmpty()) {
            return;
        }
        
        // 1. Actualizar el flag en memoria
        consumos.forEach(consumo -> {
            if (!consumo.isFacturado()) {
                consumo.setFacturado(true);
            }
        });
        
        // 2. Guardar los cambios masivamente en la base de datos
        consumoServicioRepository.saveAll(consumos);
    }
}
