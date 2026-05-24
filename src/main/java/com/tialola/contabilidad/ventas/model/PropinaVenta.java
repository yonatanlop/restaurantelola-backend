package com.tialola.contabilidad.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "propinas_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropinaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venta_id", nullable = false, unique = true)
    private Long ventaId;

    @Column(name = "monto_sugerido", precision = 10, scale = 2)
    private BigDecimal montoSugerido = BigDecimal.ZERO;

    @Column(name = "monto_registrado", precision = 10, scale = 2)
    private BigDecimal montoRegistrado = BigDecimal.ZERO;

    @Column(name = "porcentaje_sugerido", precision = 5, scale = 2)
    private BigDecimal porcentajeSugerido = BigDecimal.ZERO;

    @Column(length = 20)
    private String estado = "DESHABILITADA";

    @Column(name = "metodo_registro", length = 30)
    private String metodoRegistro;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}

