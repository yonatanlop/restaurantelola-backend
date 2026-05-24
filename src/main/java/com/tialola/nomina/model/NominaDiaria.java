package com.tialola.nomina.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nomina_diaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaDiaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, PAGADO
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(name = "registrado_por")
    private Long registradoPor;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Transient
    private String empleadoNombre;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
