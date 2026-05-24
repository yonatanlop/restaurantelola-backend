package com.tialola.auditoria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(name = "usuario_nombre", length = 100)
    private String usuarioNombre;
    
    @Column(nullable = false, length = 50)
    private String accion; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.
    
    @Column(nullable = false, length = 50)
    private String entidad; // VENTA, PLATO, INSUMO, EMPLEADO, etc.
    
    @Column(name = "entidad_id")
    private Long entidadId;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "datos_anteriores", columnDefinition = "TEXT")
    private String datosAnteriores; // JSON
    
    @Column(name = "datos_nuevos", columnDefinition = "TEXT")
    private String datosNuevos; // JSON
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(length = 20)
    private String resultado = "EXITOSO"; // EXITOSO, FALLIDO
    
    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;
    
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}
