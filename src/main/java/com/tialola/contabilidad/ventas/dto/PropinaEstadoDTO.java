package com.tialola.contabilidad.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropinaEstadoDTO {
    private boolean habilitada;
    private String mensaje;
    private String proximoPaso;
    private String version;
}

