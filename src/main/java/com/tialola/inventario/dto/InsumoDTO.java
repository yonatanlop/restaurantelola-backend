package com.tialola.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal cantidadActual;
    private BigDecimal cantidadMinima;
    private BigDecimal precioUnitario;
    private Boolean activo;
    private Boolean bajoStock;
}
