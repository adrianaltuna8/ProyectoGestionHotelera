package com.GestionHotelera.Administracion.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.Servicio;
import com.GestionHotelera.Administracion.Repository.ServicioRepository;

@Service
public class ServicioService {
    @Autowired
    private ServicioRepository servicioRepository;

    // Obtener solo servicios activos (flgEstado = 1)
    public List<Servicio> obtenerTodosActivos() {
        return servicioRepository.findAllByFlgEstado(1);
    }
    
    // Método anterior para obtener todos (útil para listados)
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepository.findById(id);
    }

    // Guardar (Crea o Actualiza) con lógica de estado
    public Servicio guardar(Servicio servicio) {
        // Lógica consistente: si no viene el estado (checkbox desmarcado en edición), se asume 0.
        // Si es un nuevo servicio, se asume 1 (activo) si no se especifica.
        if (servicio.getFlgEstado() == null) {
             servicio.setFlgEstado(1); // Nuevo por defecto activo
        }
        return servicioRepository.save(servicio);
    }

    // Eliminar (Soft Delete)
    public void eliminar(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setFlgEstado(0); // Marca como inactivo (soft delete)
        servicioRepository.save(servicio);
    }    
}
