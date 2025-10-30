package com.GestionHotelera.Administracion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestionHotelera.Administracion.Model.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped,Long>{
    List<Huesped> findAllByFlgEstado(Integer flgEstado); // Para listar solo activos
    
    boolean existsByIdentificacion(String identificacion); // Validación de creación
    boolean existsByIdentificacionAndIdNot(String identificacion, Long id); // Validación de actualización    
}

