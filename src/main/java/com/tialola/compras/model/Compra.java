package com.tialola.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "proveedor_id")
    private Long proveedorId;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(length = 20)
    private String estado = "COMPLETADA";
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(name = "registrado_por")
    private Long registradoPor;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompraDetalle> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        fechaCreacion = LocalDateTime.now();
    }
    
    public void addDetalle(CompraDetalle detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
    }
}
