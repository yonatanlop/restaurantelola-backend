package com.tialola.impresion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cola_impresion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColaImpresion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento; // VENTA, COMANDA
    
    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;
    
    @Column(name = "impresora_id")
    private Long impresoraId;
    
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, IMPRIMIENDO, COMPLETADO, ERROR
    
    @Column(name = "intentos")
    private Integer intentos = 0;
    
    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;
    
    @Column(name = "contenido_ticket", columnDefinition = "TEXT")
    private String contenidoTicket;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_impresion")
    private LocalDateTime fechaImpresion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
