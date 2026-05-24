package com.tialola.mesas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comanda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mesa_id", nullable = false)
    private Long mesaId;
    
    @Column(name = "venta_id")
    private Long ventaId;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PREPARACION, LISTA, SERVIDA, CANCELADA
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_preparacion")
    private LocalDateTime fechaPreparacion;
    
    @Column(name = "fecha_lista")
    private LocalDateTime fechaLista;
    
    @Column(name = "fecha_servida")
    private LocalDateTime fechaServida;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaDetalle> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    public void addDetalle(ComandaDetalle detalle) {
        detalles.add(detalle);
        detalle.setComanda(this);
    }
}
