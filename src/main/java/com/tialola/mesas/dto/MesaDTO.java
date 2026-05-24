package com.tialola.mesas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaDTO {
    private Long id;
    private String numero;
    private Integer capacidad;
    private String ubicacion;
    private String estado;
    private Long ventaActualId;
    private LocalDateTime horaOcupacion;
    private BigDecimal totalCuenta;
    private Integer tiempoOcupacion; // minutos
}
