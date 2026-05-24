package com.tialola.caja.service;

import com.tialola.auth.model.Usuario;
import com.tialola.auth.repository.UsuarioRepository;
import com.tialola.caja.dto.*;
import com.tialola.caja.model.CierreCaja;
import com.tialola.caja.model.DetalleArqueo;
import com.tialola.caja.repository.CierreCajaRepository;
import com.tialola.caja.repository.DetalleArqueoRepository;
import com.tialola.contabilidad.model.MovimientoCaja;
import com.tialola.contabilidad.repository.MovimientoCajaRepository;
import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CierreCajaService {

    private final CierreCajaRepository cierreRepository;
    private final DetalleArqueoRepository detalleArqueoRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public CierreCajaDetalleDTO iniciarCierre(CierreCajaAperturaDTO apertura) {
        LocalDate fecha = apertura.getFecha() != null ? apertura.getFecha() : LocalDate.now();
        BigDecimal saldoInicial = defaultValue(apertura.getSaldoInicial());
        validarUsuarioDueno(apertura.getUsuarioId());

        cierreRepository.findByFechaAndEstado(fecha, "ABIERTO")
                .ifPresent(c -> {
                    throw new RuntimeException("Ya existe un cierre abierto para esta fecha");
                });

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);

        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByFechaBetweenOrderByFechaDesc(inicio, fin);

        BigDecimal ingresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoEsperado = saldoInicial.add(totalVentas).add(ingresos).subtract(egresos);
        // El efectivo esperado es el saldo esperado (asumiendo que todo es efectivo inicialmente)
        // Se actualizará cuando se cierre el cierre con el conteo real
        BigDecimal efectivoEsperado = saldoEsperado;
        
        // Calcular total de ventas del día y por método de pago
        List<Venta> ventasDelDia = ventaRepository.findByFechaBetween(inicio, fin);
        BigDecimal totalVentas = ventasDelDia.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular ventas por método de pago
        BigDecimal ventasEfectivo = ventasDelDia.stream()
                .filter(v -> "EFECTIVO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal ventasCredito = ventasDelDia.stream()
                .filter(v -> "CREDITO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal ventasTransferencia = ventasDelDia.stream()
                .filter(v -> "TRANSFERENCIA".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CierreCaja cierre = new CierreCaja();
        cierre.setFecha(fecha);
        cierre.setUsuarioId(apertura.getUsuarioId());
        cierre.setUsuarioNombre(apertura.getUsuarioNombre());
        cierre.setSaldoInicial(saldoInicial);
        cierre.setTotalIngresos(ingresos);
        cierre.setTotalEgresos(egresos);
        cierre.setTotalVentas(totalVentas);
        cierre.setSaldoEsperado(saldoEsperado);
        cierre.setEfectivoEsperado(efectivoEsperado);
        cierre.setEfectivoContado(BigDecimal.ZERO); // Se establecerá al cerrar
        // Establecer valores automáticos por método de pago
        cierre.setTarjetas(ventasCredito); // Ahora representa créditos
        cierre.setTransferencias(ventasTransferencia);
        cierre.setOtrosMedios(ventasEfectivo); // Ahora representa efectivo de ventas
        cierre.setDiferencia(BigDecimal.ZERO); // Se calculará al cerrar
        cierre.setEstado("ABIERTO");
        cierre.setFechaApertura(LocalDateTime.now());

        return mapToDetalleDTO(cierreRepository.save(cierre));
    }

    @Transactional
    public CierreCajaDetalleDTO completarCierre(Long cierreId, CierreCajaDTO dto) {
        CierreCaja cierre = cierreRepository.findById(cierreId)
                .orElseThrow(() -> new RuntimeException("Cierre no encontrado"));

        if ("CERRADO".equals(cierre.getEstado())) {
            throw new RuntimeException("Este cierre ya está cerrado");
        }

        BigDecimal efectivoContado = defaultValue(dto.getEfectivoContado());
        validarUsuarioDueno(dto.getUsuarioId());

        // Solo actualizar el efectivo contado (lo demás ya está calculado automáticamente)
        cierre.setEfectivoContado(efectivoContado);
        // tarjetas, transferencias y otrosMedios ya tienen los valores correctos del iniciarCierre

        BigDecimal totalContado = efectivoContado
                .add(cierre.getTarjetas())
                .add(cierre.getTransferencias());

        cierre.setTotalContado(totalContado);
        cierre.setDiferencia(totalContado.subtract(cierre.getSaldoEsperado()));
        cierre.setObservaciones(dto.getObservaciones());
        cierre.setEstado("CERRADO");
        cierre.setFechaCierre(LocalDateTime.now());

        if (dto.getArqueoDetalle() != null) {
            guardarDetalleArqueo(cierreId, dto.getArqueoDetalle());
        }

        return mapToDetalleDTO(cierreRepository.save(cierre));
    }

    private void guardarDetalleArqueo(Long cierreId, Map<String, Integer> arqueo) {
        detalleArqueoRepository.deleteByCierreId(cierreId);

        for (Map.Entry<String, Integer> entry : arqueo.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                DetalleArqueo detalle = new DetalleArqueo();
                detalle.setCierreId(cierreId);
                detalle.setDenominacion(entry.getKey());
                detalle.setCantidad(entry.getValue());

                BigDecimal valor = new BigDecimal(entry.getKey());
                BigDecimal subtotal = valor.multiply(BigDecimal.valueOf(entry.getValue()));
                detalle.setSubtotal(subtotal);

                detalleArqueoRepository.save(detalle);
            }
        }
    }

    public CierreCajaEstadoDTO obtenerEstadoCierre() {
        LocalDate hoy = LocalDate.now();
        Optional<CierreCaja> cierreAbierto = cierreRepository.findByFechaAndEstado(hoy, "ABIERTO");
        
        // Si hay un cierre abierto, actualizar sus valores con las ventas actuales
        if (cierreAbierto.isPresent()) {
            actualizarValoresCierreAbierto(cierreAbierto.get());
        }
        
        Optional<CierreCaja> ultimoCierre = cierreRepository.findTopByOrderByFechaDesc();

        List<AlertaCierreDTO> alertas = new ArrayList<>();
        long diasSinCierre = 0;

        if (ultimoCierre.isEmpty()) {
            alertas.add(AlertaCierreDTO.builder()
                    .codigo("SIN_CIERRE")
                    .severidad("CRITICAL")
                    .mensaje("Nunca se ha realizado un cierre de caja. Realiza el primero hoy mismo.")
                    .build());
        } else {
            diasSinCierre = ChronoUnit.DAYS.between(ultimoCierre.get().getFecha(), hoy);
            if (diasSinCierre > 1) {
                alertas.add(AlertaCierreDTO.builder()
                        .codigo("DIAS_PENDIENTES")
                        .severidad("WARNING")
                        .fechaReferencia(ultimoCierre.get().getFecha())
                        .mensaje(String.format("Hay %d días sin cierre desde el %s.", diasSinCierre - 1, ultimoCierre.get().getFecha()))
                        .build());
            }
        }

        Optional<Venta> ultimaVenta = ventaRepository.findTopByOrderByFechaDesc();
        boolean ventasPosteriores = false;
        LocalDateTime fechaUltimaVentaPosterior = null;

        if (ultimoCierre.isPresent() && ultimaVenta.isPresent()) {
            if (ultimaVenta.get().getFecha().toLocalDate().isAfter(ultimoCierre.get().getFecha())) {
                ventasPosteriores = true;
                fechaUltimaVentaPosterior = ultimaVenta.get().getFecha();
                alertas.add(AlertaCierreDTO.builder()
                        .codigo("VENTAS_POSTERIORES")
                        .severidad("INFO")
                        .fechaVenta(fechaUltimaVentaPosterior)
                        .mensaje("Existen ventas registradas después del último cierre.")
                        .build());
            }
        }

        return CierreCajaEstadoDTO.builder()
                .cierreAbierto(cierreAbierto.isPresent())
                .cierreActual(cierreAbierto.map(this::mapToDetalleDTO).orElse(null))
                .ultimoCierre(ultimoCierre.map(this::mapToDetalleDTO).orElse(null))
                .diasSinCierre(Math.max(diasSinCierre - 1, 0))
                .tieneVentasPosteriores(ventasPosteriores)
                .ultimaVentaPosterior(fechaUltimaVentaPosterior)
                .alertas(alertas)
                .build();
    }

    public List<CierreCajaDetalleDTO> obtenerCierresPorRango(LocalDate inicio, LocalDate fin) {
        return cierreRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin).stream()
                .map(this::mapToDetalleDTO)
                .collect(Collectors.toList());
    }

    public CierreCajaDetalleDTO obtenerCierrePorFecha(LocalDate fecha) {
        return cierreRepository.findByFechaAndEstado(fecha, "CERRADO")
                .map(this::mapToDetalleDTO)
                .orElse(null);
    }

    public List<DetalleArqueoDTO> obtenerDetalleArqueo(Long cierreId) {
        return detalleArqueoRepository.findByCierreId(cierreId).stream()
                .map(this::mapDetalleArqueoDTO)
                .collect(Collectors.toList());
    }

    public CierreCajaDetalleDTO obtenerUltimoCierre() {
        return cierreRepository.findTopByOrderByFechaDesc()
                .map(this::mapToDetalleDTO)
                .orElse(null);
    }

    public CierreCajaReporteDTO generarReporte(Long cierreId) {
        CierreCaja cierre = cierreRepository.findById(cierreId)
                .orElseThrow(() -> new RuntimeException("Cierre no encontrado"));

        return CierreCajaReporteDTO.builder()
                .resumen(mapToDetalleDTO(cierre))
                .arqueo(obtenerDetalleArqueo(cierreId))
                .build();
    }

    private CierreCajaDetalleDTO mapToDetalleDTO(CierreCaja cierre) {
        return CierreCajaDetalleDTO.builder()
                .id(cierre.getId())
                .fecha(cierre.getFecha())
                .usuarioId(cierre.getUsuarioId())
                .usuarioNombre(cierre.getUsuarioNombre())
                .saldoInicial(cierre.getSaldoInicial())
                .totalIngresos(cierre.getTotalIngresos())
                .totalEgresos(cierre.getTotalEgresos())
                .saldoEsperado(cierre.getSaldoEsperado())
                .efectivoContado(cierre.getEfectivoContado())
                .tarjetas(cierre.getTarjetas())
                .transferencias(cierre.getTransferencias())
                .otrosMedios(cierre.getOtrosMedios())
                .totalContado(cierre.getTotalContado())
                .diferencia(cierre.getDiferencia())
                .estado(cierre.getEstado())
                .fechaApertura(cierre.getFechaApertura())
                .fechaCierre(cierre.getFechaCierre())
                .build();
    }

    private DetalleArqueoDTO mapDetalleArqueoDTO(DetalleArqueo detalle) {
        return new DetalleArqueoDTO(detalle.getDenominacion(), detalle.getCantidad(), detalle.getSubtotal());
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    @Transactional
    private void actualizarValoresCierreAbierto(CierreCaja cierre) {
        LocalDate fecha = cierre.getFecha();
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);

        // Recalcular ventas por método de pago con datos actuales
        List<Venta> ventasDelDia = ventaRepository.findByFechaBetween(inicio, fin);
        
        BigDecimal totalVentas = ventasDelDia.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal ventasEfectivo = ventasDelDia.stream()
                .filter(v -> "EFECTIVO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal ventasCredito = ventasDelDia.stream()
                .filter(v -> "CREDITO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal ventasTransferencia = ventasDelDia.stream()
                .filter(v -> "TRANSFERENCIA".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recalcular movimientos de caja
        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByFechaBetweenOrderByFechaDesc(inicio, fin);

        BigDecimal ingresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoEsperado = cierre.getSaldoInicial().add(totalVentas).add(ingresos).subtract(egresos);

        // Actualizar valores en el cierre
        cierre.setTotalVentas(totalVentas);
        cierre.setTotalIngresos(ingresos);
        cierre.setTotalEgresos(egresos);
        cierre.setSaldoEsperado(saldoEsperado);
        cierre.setEfectivoEsperado(saldoEsperado);
        cierre.setTarjetas(ventasCredito);
        cierre.setTransferencias(ventasTransferencia);
        cierre.setOtrosMedios(ventasEfectivo);

        // Guardar cambios
        cierreRepository.save(cierre);
    }

    private void validarUsuarioDueno(Long usuarioId) {
        if (usuarioId == null) {
            throw new RuntimeException("Se requiere el usuario que realiza la operación.");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para cierre de caja"));

        if (!"DUENO".equalsIgnoreCase(usuario.getRol()) && !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            throw new RuntimeException("Solo el dueño puede realizar el cierre de caja diario.");
        }
    }
}
