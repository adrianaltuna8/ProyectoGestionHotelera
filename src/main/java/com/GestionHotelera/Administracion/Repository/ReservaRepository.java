package com.GestionHotelera.Administracion.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.GestionHotelera.Administracion.Model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva,Long>{

    
// 1. Llegadas de Hoy (Entrada = Hoy, Estado = Pendiente o Confirmada)
    // Ordenar por fechaEntrada (Ascendente) es por defecto si no se especifica.
    List<Reserva> findByFechaEntradaAndEstadoReservaInOrderByFechaEntradaAsc(LocalDate fechaEntrada, List<String> estados);

    // 2. Huéspedes Alojados (Estado = Check-In)
    List<Reserva> findByEstadoReservaOrderByFechaSalidaAsc(String estadoReserva);

    // 3. Salidas de Hoy (Salida = Hoy, Estado = Check-In)
    List<Reserva> findByFechaSalidaAndEstadoReservaOrderByFechaSalidaAsc(LocalDate fechaSalida, String estadoReserva);

    // 4. Próximas Llegadas (Entrada > Hoy, Estado = Pendiente o Confirmada)
    List<Reserva> findByFechaEntradaAfterAndEstadoReservaInOrderByFechaEntradaAsc(LocalDate fechaEntrada, List<String> estados);

    // 5. Historial (Estados Check-Out o Cancelada)
    List<Reserva> findByEstadoReservaInOrderByFechaSalidaDesc(List<String> estados);

    
@Query("SELECT r FROM Reserva r WHERE " +
           "r.habitacion.id = :habitacionId AND " + 
           "r.estadoReserva IN ('Pendiente', 'Confirmada', 'Check-In') AND " + 
           "r.id <> :idExcluir AND " + 
           "(r.fechaEntrada < :fechaSalida AND r.fechaSalida > :fechaEntrada)")
    List<Reserva> findConflictosDeReserva(
            Long habitacionId, 
            LocalDate fechaEntrada, 
            LocalDate fechaSalida,
            Long idExcluir
    );    

    List<Reserva> findByEstadoReservaInOrderByFechaEntradaAsc(List<String> estados);    
}
