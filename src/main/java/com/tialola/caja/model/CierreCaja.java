package com.tialola.caja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierres_caja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CierreCaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "usuario_nombre", length = 100)
    private String usuarioNombre;
    
    // Montos del sistema
    @Column(name = "saldo_inicial", precision = 10, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;
    
    @Column(name = "total_ingresos", precision = 10, scale = 2)
    private BigDecimal totalIngresos = BigDecimal.ZERO;
    
    @Column(name = "total_egresos", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEgresos = BigDecimal.ZERO;
    
    @Column(name = "saldo_esperado", precision = 10, scale = 2)
    private BigDecimal saldoEsperado = BigDecimal.ZERO;
    
    @Column(name = "efectivo_esperado", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoEsperado = BigDecimal.ZERO;
    
    // Arqueo físico
    @Column(name = "efectivo_contado", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoContado = BigDecimal.ZERO;
    
    @Column(name = "tarjetas", precision = 10, scale = 2)
    private BigDecimal tarjetas = BigDecimal.ZERO;
    
    @Column(name = "transferencias", precision = 10, scale = 2)
    private BigDecimal transferencias = BigDecimal.ZERO;
    
    @Column(name = "otros_medios", precision = 10, scale = 2)
    private BigDecimal otrosMedios = BigDecimal.ZERO;
    
    @Column(name = "total_contado", precision = 10, scale = 2)
    private BigDecimal totalContado = BigDecimal.ZERO;
    
    // Diferencia
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal diferencia = BigDecimal.ZERO;
    
    @Column(name = "total_ventas", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentas = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(nullable = false, length = 20)
    private String estado = "ABIERTO"; // ABIERTO, CERRADO
    
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @PrePersist
    protected void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
    }
}
