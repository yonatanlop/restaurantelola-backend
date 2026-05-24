package com.tialola.impresion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private String tipoTicket; // VENTA, COMANDA
    private Long ventaId;
    private LocalDateTime fecha;
    private String cajero;
    private List<ItemTicket> items;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal propina;
    private BigDecimal total;
    private String metodoPago;
    private String notas;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemTicket {
        private String nombrePlato;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String notas;
    }
}
