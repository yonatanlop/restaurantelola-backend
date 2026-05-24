package com.tialola.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private String notas;
    private Long registradoPor;
    private List<CompraDetalleDTO> detalles;
}
