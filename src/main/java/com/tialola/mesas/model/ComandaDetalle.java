package com.tialola.mesas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "comanda_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;
    
    @Column(name = "plato_id", nullable = false)
    private Long platoId;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PREPARACION, LISTO, SERVIDO, CANCELADO
    
    @Column(columnDefinition = "TEXT")
    private String notas;
}
