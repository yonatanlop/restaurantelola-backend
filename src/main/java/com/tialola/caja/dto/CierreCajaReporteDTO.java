package com.tialola.caja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCajaReporteDTO {
    private CierreCajaDetalleDTO resumen;
    @Builder.Default
    private List<DetalleArqueoDTO> arqueo = Collections.emptyList();
}

