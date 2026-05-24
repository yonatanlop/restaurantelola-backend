package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleArqueoDTO {
    private String denominacion;
    private Integer cantidad;
    private BigDecimal subtotal;
}

