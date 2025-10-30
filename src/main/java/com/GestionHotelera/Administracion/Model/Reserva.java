package com.GestionHotelera.Administracion.Model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaEntrada;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaSalida;
    private double precioTotal;
    private String estadoReserva;

    // Relación Muchos a Uno: Una reserva tiene un solo huésped
    @ManyToOne
    @JoinColumn(name = "huesped_id")
    private Huesped huesped;

    // Relación Muchos a Uno: Una reserva tiene una sola habitación
    @ManyToOne
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;

    // Relación Muchos a Uno: Una reserva es creada por un solo usuario del sistema
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
