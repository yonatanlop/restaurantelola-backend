package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCajaDTO {
    private LocalDate fecha;
    private Long usuarioId;
    private String usuarioNombre;
    private BigDecimal saldoInicial;
    private BigDecimal efectivoContado;
    private BigDecimal tarjetas;
    private BigDecimal transferencias;
    private BigDecimal otrosMedios;
    private String observaciones;
    private Map<String, Integer> arqueoDetalle; // denominacion -> cantidad
}
