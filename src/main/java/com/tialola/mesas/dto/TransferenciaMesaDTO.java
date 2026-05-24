package com.tialola.mesas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaMesaDTO {
    private Long mesaOrigenId;
    private Long mesaDestinoId;
    private Long ventaId;
    private Long usuarioId;
    private String motivo;
}
