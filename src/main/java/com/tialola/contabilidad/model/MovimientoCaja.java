package com.tialola.contabilidad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_caja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false, length = 20)
    private String tipo; // INGRESO, EGRESO
    
    @Column(nullable = false, length = 50)
    private String concepto; // VENTA, COMPRA, NOMINA, AJUSTE
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(name = "referencia_id")
    private Long referenciaId;
    
    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo; // VENTA, COMPRA, NOMINA
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
