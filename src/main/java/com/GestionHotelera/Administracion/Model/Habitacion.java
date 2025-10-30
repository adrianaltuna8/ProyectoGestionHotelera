package com.GestionHotelera.Administracion.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "habitaciones")
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String tipo;
    private int capacidad;
    private double precioPorNoche;

    // Estado de la habitación: Disponible, Ocupada, En Limpieza, etc.
    private String estado;

    private Integer flgEstado=1; //1=activo , 0 = inactivo (registro "eliminado")

    // Una habitación puede tener muchas reservas
    @OneToMany(mappedBy = "habitacion")
    private List<Reserva> reservas = new ArrayList<>();
}
