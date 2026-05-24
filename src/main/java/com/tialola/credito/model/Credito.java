package com.tialola.credito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "creditos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "venta_id")
    private Long ventaId;
    
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    @Column(name = "valor_pedido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPedido;
    
    @Column(nullable = false)
    private Boolean pagado = false;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "usuario_registro_id")
    private Long usuarioRegistroId;
    
    @Column(name = "usuario_pago_id")
    private Long usuarioPagoId;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @PrePersist
    protected void onCreate() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }
}
