package com.tialola.contabilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenCajaDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldo;
    private Integer cantidadMovimientos;
    
    // Desglose de ingresos
    private BigDecimal ingresosVentas;
    
    // Desglose de egresos
    private BigDecimal egresosCompras;
    private BigDecimal egresosNomina;
    private BigDecimal egresosOtros;
}
