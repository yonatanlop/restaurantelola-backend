package com.tialola.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;
    
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE
    
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;
    
    @Column(name = "cantidad_anterior", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidadAnterior;
    
    @Column(name = "cantidad_nueva", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidadNueva;
    
    @Column(length = 50)
    private String motivo;
    
    @Column(name = "referencia_id")
    private Long referenciaId;
    
    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}
