package com.tialola.contabilidad.service;

import com.tialola.auth.repository.UsuarioRepository;
import com.tialola.contabilidad.dto.MovimientoCajaDTO;
import com.tialola.contabilidad.dto.ResumenCajaDTO;
import com.tialola.contabilidad.model.MovimientoCaja;
import com.tialola.contabilidad.repository.MovimientoCajaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContabilidadService {
    
    private final MovimientoCajaRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<MovimientoCajaDTO> obtenerMovimientos(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<MovimientoCajaDTO> obtenerPorTipo(String tipo, LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByTipoAndFechaBetween(tipo, inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<MovimientoCajaDTO> obtenerPorConcepto(String concepto, LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByConceptoAndFechaBetween(concepto, inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public ResumenCajaDTO obtenerResumen(LocalDateTime inicio, LocalDateTime fin) {
        ResumenCajaDTO resumen = new ResumenCajaDTO();
        resumen.setFechaInicio(inicio);
        resumen.setFechaFin(fin);
        
        // Calcular totales
        BigDecimal ingresos = movimientoRepository.calcularTotalPorTipo("INGRESO", inicio, fin);
        BigDecimal egresos = movimientoRepository.calcularTotalPorTipo("EGRESO", inicio, fin);
        BigDecimal saldo = movimientoRepository.calcularSaldo(inicio, fin);
        
        resumen.setTotalIngresos(ingresos != null ? ingresos : BigDecimal.ZERO);
        resumen.setTotalEgresos(egresos != null ? egresos : BigDecimal.ZERO);
        resumen.setSaldo(saldo != null ? saldo : BigDecimal.ZERO);
        
        // Contar movimientos
        List<MovimientoCaja> movimientos = movimientoRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
        resumen.setCantidadMovimientos(movimientos.size());
        
        // Desglose de ingresos
        BigDecimal ingresosVentas = calcularPorConcepto("VENTA", inicio, fin);
        resumen.setIngresosVentas(ingresosVentas);
        
        // Desglose de egresos
        BigDecimal egresosCompras = calcularPorConcepto("COMPRA", inicio, fin);
        BigDecimal egresosNomina = calcularPorConcepto("NOMINA", inicio, fin);
        resumen.setEgresosCompras(egresosCompras);
        resumen.setEgresosNomina(egresosNomina);
        resumen.setEgresosOtros(resumen.getTotalEgresos()
            .subtract(egresosCompras)
            .subtract(egresosNomina));
        
        return resumen;
    }
    
    @Transactional
    public MovimientoCaja registrarMovimiento(String tipo, String concepto, String descripcion,
                                             BigDecimal monto, Long referenciaId, String referenciaTipo,
                                             Long usuarioId) {
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setTipo(tipo);
        movimiento.setConcepto(concepto);
        movimiento.setDescripcion(descripcion);
        movimiento.setMonto(monto);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setUsuarioId(usuarioId);
        
        return movimientoRepository.save(movimiento);
    }
    
    @Transactional
    public void registrarVenta(Long ventaId, BigDecimal monto, Long usuarioId) {
        registrarMovimiento("INGRESO", "VENTA", "Venta #" + ventaId, 
            monto, ventaId, "VENTA", usuarioId);
    }
    
    @Transactional
    public void registrarCompra(Long compraId, BigDecimal monto, Long usuarioId) {
        registrarMovimiento("EGRESO", "COMPRA", "Compra #" + compraId, 
            monto, compraId, "COMPRA", usuarioId);
    }
    
    @Transactional
    public void registrarPagoNomina(Long nominaId, BigDecimal monto, Long usuarioId) {
        registrarMovimiento("EGRESO", "NOMINA", "Pago nómina #" + nominaId, 
            monto, nominaId, "NOMINA", usuarioId);
    }
    
    private BigDecimal calcularPorConcepto(String concepto, LocalDateTime inicio, LocalDateTime fin) {
        List<MovimientoCaja> movimientos = movimientoRepository
            .findByConceptoAndFechaBetween(concepto, inicio, fin);
        return movimientos.stream()
            .map(MovimientoCaja::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private MovimientoCajaDTO toDTO(MovimientoCaja movimiento) {
        MovimientoCajaDTO dto = new MovimientoCajaDTO();
        dto.setId(movimiento.getId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipo(movimiento.getTipo());
        dto.setConcepto(movimiento.getConcepto());
        dto.setDescripcion(movimiento.getDescripcion());
        dto.setMonto(movimiento.getMonto());
        dto.setReferenciaId(movimiento.getReferenciaId());
        dto.setReferenciaTipo(movimiento.getReferenciaTipo());
        dto.setUsuarioId(movimiento.getUsuarioId());
        
        if (movimiento.getUsuarioId() != null) {
            usuarioRepository.findById(movimiento.getUsuarioId())
                .ifPresent(u -> dto.setUsuarioNombre(u.getNombre()));
        }
        
        return dto;
    }
}
