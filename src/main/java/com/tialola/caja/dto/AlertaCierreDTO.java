package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaCierreDTO {
    private String codigo;
    private String mensaje;
    private String severidad;
    private LocalDate fechaReferencia;
    private LocalDateTime fechaVenta;
}

