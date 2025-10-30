package com.GestionHotelera.Administracion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestionHotelera.Administracion.Model.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio,Long>{
    List<Servicio> findAllByFlgEstado(Integer flgEstado);

}
