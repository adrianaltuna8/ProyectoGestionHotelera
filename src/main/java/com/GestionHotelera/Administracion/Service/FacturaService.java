package com.GestionHotelera.Administracion.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestionHotelera.Administracion.Model.ConsumoServicio;
import com.GestionHotelera.Administracion.Model.Factura;
import com.GestionHotelera.Administracion.Model.Reserva;
import com.GestionHotelera.Administracion.Repository.ConsumoServicioRepository;
import com.GestionHotelera.Administracion.Repository.FacturaRepository;

import jakarta.transaction.Transactional;

@Service
public class FacturaService {
    private static final double TASA_IMPUESTOS = 0.18; // 18% IGV

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ConsumoServicioRepository consumoServicioRepository;

    // =========================================================
    // CRUD Básico
    // =========================================================

    public List<Factura> obtenerTodas() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc();
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    // =========================================================
    // Lógica de Negocio Principal
    // =========================================================

    /**
     * Genera la factura final de una reserva.
     * Se ejecuta automáticamente durante el Check-Out.
     */
    @Transactional
    public Factura generarFacturaFinal(Reserva reserva) {

        // Evitar duplicados
        if (facturaRepository.findByReserva(reserva).isPresent()) {
            throw new IllegalStateException("Ya existe una factura para esta reserva: " + reserva.getId());
        }

        // Obtener consumos no facturados
        List<ConsumoServicio> consumosPendientes =
                consumoServicioRepository.findByReservaAndFacturadoFalse(reserva);

        // Calcular montos
        double montoServicios = consumosPendientes.stream()
                .mapToDouble(ConsumoServicio::getSubtotal)
                .sum();

        double montoReserva = reserva.getPrecioTotal();
        double subtotal = montoReserva + montoServicios;
        double impuestos = subtotal * TASA_IMPUESTOS;
        double total = subtotal + impuestos;

        // 3️⃣ Crear y guardar la factura
        Factura factura = new Factura();
        factura.setReserva(reserva);
        factura.setMontoReserva(montoReserva);
        factura.setMontoServicios(montoServicios);
        factura.setSubtotal(redondear(subtotal));
        factura.setImpuestos(redondear(impuestos));
        factura.setMontoTotal(redondear(total));
        factura.setEstadoPago("Pendiente");

        Factura nuevaFactura = facturaRepository.save(factura);

        // 4️⃣ Vincular los consumos
        for (ConsumoServicio consumo : consumosPendientes) {
            consumo.setFactura(nuevaFactura);
            consumo.setFacturado(true);
            consumoServicioRepository.save(consumo);
        }

        nuevaFactura.setConsumos(consumosPendientes);
        return nuevaFactura;
    }

    @Transactional
    public Factura marcarComoPagada(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada."));
        factura.setEstadoPago("Pagada");
        return facturaRepository.save(factura);
    }

    // =========================================================
    // Utilidades
    // =========================================================

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
