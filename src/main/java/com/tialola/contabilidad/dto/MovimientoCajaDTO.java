package com.tialola.contabilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaDTO {
    private Long id;
    private LocalDateTime fecha;
    private String tipo;
    private String concepto;
    private String descripcion;
    private BigDecimal monto;
    private Long referenciaId;
    private String referenciaTipo;
    private Long usuarioId;
    private String usuarioNombre;
}
