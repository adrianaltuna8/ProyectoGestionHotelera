package com.GestionHotelera.Administracion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GestionHotelera.Administracion.Model.Habitacion;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion,Long>{
    boolean existsByNumero(String numero);
    boolean existsByNumeroAndIdNot(String numero, Long id);    
    List<Habitacion> findByEstadoNotIn(List<String> estadosExcluidos);
}

