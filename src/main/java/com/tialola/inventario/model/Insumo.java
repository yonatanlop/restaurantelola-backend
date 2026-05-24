package com.tialola.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insumos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;
    
    @Column(name = "cantidad_actual", precision = 10, scale = 3)
    private BigDecimal cantidadActual = BigDecimal.ZERO;
    
    @Column(name = "cantidad_minima", precision = 10, scale = 3)
    private BigDecimal cantidadMinima = BigDecimal.ZERO;
    
    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
    
    @Transient
    public boolean isBajoStock() {
        return cantidadActual.compareTo(cantidadMinima) <= 0;
    }
}
