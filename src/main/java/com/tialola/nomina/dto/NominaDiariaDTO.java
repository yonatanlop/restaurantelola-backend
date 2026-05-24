package com.tialola.nomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaDiariaDTO {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String empleadoPuesto;
    private LocalDate fecha;
    private BigDecimal monto;
    private String estado;
    private LocalDateTime fechaPago;
    private String notas;
    private Long registradoPor;
}
