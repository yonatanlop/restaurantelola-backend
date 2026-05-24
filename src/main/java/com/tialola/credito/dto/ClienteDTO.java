package com.tialola.credito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String notas;
    private BigDecimal deudaTotal;
    private Integer creditosPendientes;
}
