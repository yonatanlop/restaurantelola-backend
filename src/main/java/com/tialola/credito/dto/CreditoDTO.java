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
public class CreditoDTO {
    private Long clienteId;
    private Long ventaId;
    private BigDecimal valorPedido;
    private String descripcion;
    private String notas;
    private Long usuarioRegistroId;
}
