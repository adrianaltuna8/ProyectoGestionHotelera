package com.GestionHotelera.Administracion.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Huesped;
import com.GestionHotelera.Administracion.Repository.HuespedRepository;

@Service
public class HuespedService {
    @Autowired
    private HuespedRepository huespedRepository;

    // Obtener solo huéspedes activos (flgEstado = 1)
    public List<Huesped> obtenerTodosActivos() {
        return huespedRepository.findAllByFlgEstado(1);
    }

    public Optional<Huesped> obtenerPorId(Long id) {
        return huespedRepository.findById(id);
    }

    // Guardar (Crea o Actualiza)
    public Huesped guardar(Huesped huesped) {
        if (huesped.getFlgEstado() == null) {
            huesped.setFlgEstado(0); // Asegura que los nuevos se creen como activos
        }
        return huespedRepository.save(huesped);
    }

    // Eliminar (Soft Delete)
    public void eliminar(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Huésped no encontrado"));
        huesped.setFlgEstado(0); // Marca como inactivo (soft delete)
        huespedRepository.save(huesped);
    }

    // Métodos de validación de unicidad
    public boolean existeIdentificacion(String identificacion) {
        return huespedRepository.existsByIdentificacion(identificacion);
    }

    public boolean existeIdentificacionForUpdate(String identificacion, Long id) {
        return huespedRepository.existsByIdentificacionAndIdNot(identificacion, id);
    }    
}
