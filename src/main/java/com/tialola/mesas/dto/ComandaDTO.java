package com.tialola.mesas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComandaDTO {
    private Long mesaId;
    private String numeroMesa;
    private Long usuarioId;
    private String notas;
    private List<ItemComanda> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemComanda {
        private Long platoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private String notas;
    }
}
