package com.tialola.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String tipoComida;
    private Boolean activo;
}
