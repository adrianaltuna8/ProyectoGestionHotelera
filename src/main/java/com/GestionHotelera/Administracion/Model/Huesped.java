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
@Table(name = "huespedes")
public class Huesped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Integer flgEstado=1; //1=activo , 0 = inactivo (registro "eliminado")
    private String identificacion;
    //Un huésped puede tener muchas reservas
    @OneToMany(mappedBy = "huesped")
    private List<Reserva> reservas = new ArrayList<>();
}
