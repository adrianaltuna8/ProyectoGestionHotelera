package com.GestionHotelera.Administracion.Model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@Table(name = "consumos_servicio")
public class ConsumoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciones (Obligatorias)
    // ----------------------------------------------------
    // Reserva a la que se carga el consumo (Crucial para la liquidación)
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    // El tipo de servicio consumido
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    
    // Datos Financieros y de Trazabilidad (Mejoras Clave)
    // ----------------------------------------------------
    private Integer cantidad; // La cantidad consumida
    
    // 💡 CLAVE: Precio Unitario al momento del consumo. Esto evita errores 
    // si el precio del Servicio cambia antes del Check-Out.
    private double precioUnitario; 
    
    // Fecha en que se registró el consumo (Importante para reportes)
    private LocalDate fechaConsumo = LocalDate.now();
    
    // 💡 CLAVE: Flag para evitar que el mismo consumo se facture dos veces.
    private boolean facturado = false; 

    // Constructor vacío requerido por JPA (Lombok lo genera con @NoArgsConstructor)
    
    // Método de Negocio
    // ----------------------------------------------------
    public double getSubtotal() {
        return this.cantidad * this.precioUnitario;
    }
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "factura_id") // La columna de la clave foránea en la tabla consumos_servicio
    private Factura factura; // ¡Este campo es el que necesita el setFactura()!    
}
