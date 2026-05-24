package com.tialola.nomina.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(unique = true, length = 50)
    private String documento;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Column(length = 50)
    private String puesto;
    
    @Column(name = "salario_diario", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioDiario;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;
    
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
    
    @Transient
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
