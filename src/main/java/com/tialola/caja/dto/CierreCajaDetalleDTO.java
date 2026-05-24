package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCajaDetalleDTO {
    private Long id;
    private LocalDate fecha;
    private Long usuarioId;
    private String usuarioNombre;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoEsperado;
    private BigDecimal efectivoContado;
    private BigDecimal tarjetas;
    private BigDecimal transferencias;
    private BigDecimal otrosMedios;
    private BigDecimal totalContado;
    private BigDecimal diferencia;
    private String estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
}

