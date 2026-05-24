package com.tialola.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private ResumenVentas ventasHoy;
    private ResumenVentas ventasSemana;
    private ResumenVentas ventasMes;
    private List<PlatoPopular> topPlatos;
    private List<AlertaStock> alertasStock;
    private EstadoCaja estadoCaja;
    private List<TendenciaVentas> tendenciasSemanal;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenVentas {
        private BigDecimal totalVentas;
        private Integer cantidadOrdenes;
        private BigDecimal ticketPromedio;
        private BigDecimal variacionPorcentual; // vs período anterior
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatoPopular {
        private Long platoId;
        private String nombrePlato;
        private Integer cantidadVendida;
        private BigDecimal totalVentas;
        private String categoria;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertaStock {
        private Long insumoId;
        private String nombreInsumo;
        private BigDecimal cantidadActual;
        private BigDecimal stockMinimo;
        private String unidadMedida;
        private String nivelAlerta; // CRITICO, BAJO, MEDIO
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadoCaja {
        private BigDecimal saldoActual;
        private BigDecimal ingresosHoy;
        private BigDecimal egresosHoy;
        private LocalDate fecha;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TendenciaVentas {
        private LocalDate fecha;
        private BigDecimal totalVentas;
        private Integer cantidadOrdenes;
    }
}
