package com.tialola.caja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_arqueo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleArqueo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cierre_id", nullable = false)
    private Long cierreId;
    
    @Column(nullable = false, length = 50)
    private String denominacion; // 200, 100, 50, 20, 10, 5, 2, 1, 0.50, 0.20, 0.10, 0.05
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
