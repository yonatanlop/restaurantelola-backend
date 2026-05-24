package com.tialola.contabilidad.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long usuarioId;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal propina;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
    private String notas;
    private String cajero;
    private List<VentaDetalleDTO> detalles;
}
