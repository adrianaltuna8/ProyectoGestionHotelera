package com.GestionHotelera.Administracion.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1:1 con Reserva
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", unique = true, nullable = false)
    private Reserva reserva;

    // Relación 1:N con Consumos
    // La lista de consumos que esta factura está liquidando.
    // Usamos mappedBy="factura" si ConsumoServicio tiene el campo 'factura'
    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY)
    private List<ConsumoServicio> consumos; 

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision = LocalDate.now();

    // Detalle de Montos (Usando double para ser consistente con Reserva y ConsumoServicio)
    private double montoReserva; // Precio de la estancia (base: Reserva.precioTotal)
    private double montoServicios; // Suma de ConsumoServicio facturados
    private double subtotal; // montoReserva + montoServicios
    private double impuestos;
    private double montoTotal; // subtotal + impuestos

    private String estadoPago; // Ej: "Pagada", "Pendiente", "Cobro Final"

}
