package com.tialola.mesas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias_mesa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaMesa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mesa_origen_id", nullable = false)
    private Long mesaOrigenId;
    
    @Column(name = "mesa_destino_id", nullable = false)
    private Long mesaDestinoId;
    
    @Column(name = "venta_id", nullable = false)
    private Long ventaId;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(columnDefinition = "TEXT")
    private String motivo;
    
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}
