package com.tialola.contabilidad.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal propina = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "metodo_pago", length = 20, nullable = false)
    private String metodoPago;
    
    @Column(length = 20)
    private String estado = "COMPLETADA";
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();
    
    @Transient
    private String cajero;
    
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        fechaCreacion = LocalDateTime.now();
    }
    
    public void addDetalle(VentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
}
