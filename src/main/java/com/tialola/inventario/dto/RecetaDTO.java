package com.tialola.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {
    private Long id;
    private Long platoId;
    private String platoNombre;
    private Long insumoId;
    private String insumoNombre;
    private String unidadMedida;
    private BigDecimal cantidadNecesaria;
}
