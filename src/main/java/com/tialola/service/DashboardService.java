package com.tialola.service;

import com.tialola.dto.DashboardDTO;
import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.model.VentaDetalle;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import com.tialola.contabilidad.ventas.repository.VentaDetalleRepository;
import com.tialola.inventario.model.Insumo;
import com.tialola.inventario.repository.InsumoRepository;
import com.tialola.contabilidad.model.MovimientoCaja;
import com.tialola.contabilidad.repository.MovimientoCajaRepository;
import com.tialola.menu.model.Plato;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final VentaDetalleRepository ventaDetalleRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final PlatoRepository platoRepository;

    public DashboardDTO obtenerDashboard() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = LocalDate.now();

        return DashboardDTO.builder()
                .ventasHoy(calcularResumenVentas(hoy.atStartOfDay(), ahora))
                .ventasSemana(calcularResumenVentasSemana(hoy))
                .ventasMes(calcularResumenVentasMes(hoy))
                .topPlatos(obtenerTopPlatos(hoy.atStartOfDay(), ahora, 5))
                .alertasStock(obtenerAlertasStock())
                .estadoCaja(obtenerEstadoCaja(hoy))
                .tendenciasSemanal(obtenerTendenciasSemanal(hoy))
                .build();
    }

    private DashboardDTO.ResumenVentas calcularResumenVentas(LocalDateTime inicio, LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
        
        BigDecimal total = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer cantidad = ventas.size();
        
        BigDecimal ticketPromedio = cantidad > 0 
                ? total.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Calcular variación vs período anterior
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        LocalDateTime inicioAnterior = inicio.minusDays(dias);
        List<Venta> ventasAnteriores = ventaRepository.findByFechaBetween(inicioAnterior, inicio);
        
        BigDecimal totalAnterior = ventasAnteriores.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal variacion = calcularVariacionPorcentual(total, totalAnterior);

        return DashboardDTO.ResumenVentas.builder()
                .totalVentas(total)
                .cantidadOrdenes(cantidad)
                .ticketPromedio(ticketPromedio)
                .variacionPorcentual(variacion)
                .build();
    }

    private DashboardDTO.ResumenVentas calcularResumenVentasSemana(LocalDate hoy) {
        LocalDateTime inicioSemana = hoy.minusDays(6).atStartOfDay();
        LocalDateTime finSemana = hoy.atTime(23, 59, 59);
        return calcularResumenVentas(inicioSemana, finSemana);
    }

    private DashboardDTO.ResumenVentas calcularResumenVentasMes(LocalDate hoy) {
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = hoy.atTime(23, 59, 59);
        return calcularResumenVentas(inicioMes, finMes);
    }

    private List<DashboardDTO.PlatoPopular> obtenerTopPlatos(LocalDateTime inicio, LocalDateTime fin, int limite) {
        List<Object[]> resultados = ventaDetalleRepository.findTopPlatosByPeriodo(inicio, fin);
        
        return resultados.stream()
                .limit(limite)
                .map(row -> {
                    Long platoId = (Long) row[0];
                    Plato plato = platoRepository.findById(platoId).orElse(null);
                    
                    return DashboardDTO.PlatoPopular.builder()
                            .platoId(platoId)
                            .nombrePlato(plato != null ? plato.getNombre() : "Plato no encontrado")
                            .cantidadVendida(((Number) row[1]).intValue())
                            .totalVentas((BigDecimal) row[2])
                            .categoria(plato != null ? plato.getTipoComida() : "N/A")
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<DashboardDTO.AlertaStock> obtenerAlertasStock() {
        List<Insumo> insumosBajoStock = insumoRepository.findInsumosBajoStock();
        
        return insumosBajoStock.stream()
                .map(insumo -> {
                    String nivelAlerta = determinarNivelAlerta(insumo);
                    return DashboardDTO.AlertaStock.builder()
                            .insumoId(insumo.getId())
                            .nombreInsumo(insumo.getNombre())
                            .cantidadActual(insumo.getCantidadActual())
                            .stockMinimo(insumo.getCantidadMinima())
                            .unidadMedida(insumo.getUnidadMedida())
                            .nivelAlerta(nivelAlerta)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String determinarNivelAlerta(Insumo insumo) {
        BigDecimal porcentaje = insumo.getCantidadActual()
                .divide(insumo.getCantidadMinima(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        if (porcentaje.compareTo(BigDecimal.valueOf(25)) <= 0) {
            return "CRITICO";
        } else if (porcentaje.compareTo(BigDecimal.valueOf(50)) <= 0) {
            return "BAJO";
        } else {
            return "MEDIO";
        }
    }

    private DashboardDTO.EstadoCaja obtenerEstadoCaja(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);
        
        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
        
        BigDecimal ingresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal egresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal saldo = ingresos.subtract(egresos);
        
        return DashboardDTO.EstadoCaja.builder()
                .saldoActual(saldo)
                .ingresosHoy(ingresos)
                .egresosHoy(egresos)
                .fecha(fecha)
                .build();
    }

    private List<DashboardDTO.TendenciaVentas> obtenerTendenciasSemanal(LocalDate hoy) {
        List<DashboardDTO.TendenciaVentas> tendencias = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.atTime(23, 59, 59);
            
            List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
            
            BigDecimal total = ventas.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            tendencias.add(DashboardDTO.TendenciaVentas.builder()
                    .fecha(fecha)
                    .totalVentas(total)
                    .cantidadOrdenes(ventas.size())
                    .build());
        }
        
        return tendencias;
    }

    private BigDecimal calcularVariacionPorcentual(BigDecimal actual, BigDecimal anterior) {
        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return actual.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        
        return actual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
