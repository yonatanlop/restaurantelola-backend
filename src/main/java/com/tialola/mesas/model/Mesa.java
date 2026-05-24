package com.tialola.mesas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String numero;
    
    @Column(nullable = false)
    private Integer capacidad;
    
    @Column(length = 50)
    private String ubicacion; // SALON, TERRAZA, VIP
    
    @Column(nullable = false, length = 20)
    private String estado = "LIBRE"; // LIBRE, OCUPADA, RESERVADA, LIMPIEZA
    
    @Column(name = "venta_actual_id")
    private Long ventaActualId;
    
    @Column(name = "hora_ocupacion")
    private LocalDateTime horaOcupacion;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}
