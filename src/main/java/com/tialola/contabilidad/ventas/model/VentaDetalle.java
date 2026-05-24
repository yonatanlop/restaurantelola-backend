package com.tialola.contabilidad.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "venta_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
    
    @Column(name = "plato_id", nullable = false)
    private Long platoId;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
}
