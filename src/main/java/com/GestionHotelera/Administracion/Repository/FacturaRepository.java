package com.GestionHotelera.Administracion.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestionHotelera.Administracion.Model.Factura;
import com.GestionHotelera.Administracion.Model.Reserva;

import groovy.util.logging.Log;

public interface FacturaRepository extends JpaRepository<Factura,Log>{
    // Ordenar las facturas de más recientes a más antiguas
    List<Factura> findAllByOrderByFechaEmisionDesc();

    // Buscar si ya existe una factura para una reserva (evita duplicados)
    Optional<Factura> findByReserva(Reserva reserva);
    Optional<Factura> findById(Long id);
    
}
