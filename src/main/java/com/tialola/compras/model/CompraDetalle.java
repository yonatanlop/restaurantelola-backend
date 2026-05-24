package com.tialola.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "compra_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;
    
    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;
    
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
