package com.tialola.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plato_id", nullable = false)
    private Long platoId;
    
    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;
    
    @Column(name = "cantidad_necesaria", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidadNecesaria;
}
