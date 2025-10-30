package com.GestionHotelera.Administracion.Model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
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
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    private String rol; //ADMIN O EMPLEADO
    private Integer flgEstado=1;    // RELACIÓN OPCIONAL: 1 Usuario → Muchas Reservas (para auditoría)
    @OneToMany(mappedBy = "usuario")
    private List<Reserva> reservas = new ArrayList<>();
}
