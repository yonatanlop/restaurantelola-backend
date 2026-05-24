package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCajaEstadoDTO {
    private boolean cierreAbierto;
    private CierreCajaDetalleDTO cierreActual;
    private CierreCajaDetalleDTO ultimoCierre;
    private long diasSinCierre;
    private boolean tieneVentasPosteriores;
    private LocalDateTime ultimaVentaPosterior;
    @Builder.Default
    private List<AlertaCierreDTO> alertas = Collections.emptyList();
}

