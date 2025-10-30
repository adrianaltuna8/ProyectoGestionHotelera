package com.GestionHotelera.Administracion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestionHotelera.Administracion.Model.ConsumoServicio;
import com.GestionHotelera.Administracion.Model.Reserva;

public interface ConsumoServicioRepository extends JpaRepository<ConsumoServicio,Long>{
// 🚨 CLAVE: Listar todos los consumos para una reserva específica
    List<ConsumoServicio> findByReservaOrderByFechaConsumoDesc(Reserva reserva);
    
    // CLAVE para Facturación: Obtener consumos pendientes de facturar
    List<ConsumoServicio> findByReservaAndFacturado(Reserva reserva, boolean facturado);

    // Método que FacturaService necesita ahora:
    List<ConsumoServicio> findByReservaAndFacturadoFalse(Reserva reserva);
}
