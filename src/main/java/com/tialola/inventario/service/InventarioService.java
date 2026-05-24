package com.tialola.inventario.service;

import com.tialola.inventario.dto.InsumoDTO;
import com.tialola.inventario.model.Insumo;
import com.tialola.inventario.model.MovimientoInventario;
import com.tialola.inventario.model.Receta;
import com.tialola.inventario.repository.InsumoRepository;
import com.tialola.inventario.repository.MovimientoInventarioRepository;
import com.tialola.inventario.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {
    
    private final InsumoRepository insumoRepository;
    private final RecetaRepository recetaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    
    public List<InsumoDTO> obtenerTodos() {
        return insumoRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<InsumoDTO> obtenerActivos() {
        return insumoRepository.findByActivoTrue().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<InsumoDTO> obtenerBajoStock() {
        return insumoRepository.findInsumosBajoStock().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<Insumo> obtenerPorId(Long id) {
        return insumoRepository.findById(id);
    }
    
    @Transactional
    public Insumo crearInsumo(InsumoDTO dto) {
        Insumo insumo = new Insumo();
        insumo.setNombre(dto.getNombre());
        insumo.setDescripcion(dto.getDescripcion());
        insumo.setUnidadMedida(dto.getUnidadMedida());
        insumo.setCantidadActual(dto.getCantidadActual() != null ? dto.getCantidadActual() : BigDecimal.ZERO);
        insumo.setCantidadMinima(dto.getCantidadMinima() != null ? dto.getCantidadMinima() : BigDecimal.ZERO);
        insumo.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : BigDecimal.ZERO);
        insumo.setActivo(true);
        
        Insumo guardado = insumoRepository.save(insumo);
        
        // Registrar movimiento inicial si hay cantidad
        if (guardado.getCantidadActual().compareTo(BigDecimal.ZERO) > 0) {
            registrarMovimiento(guardado.getId(), "ENTRADA", guardado.getCantidadActual(), 
                BigDecimal.ZERO, guardado.getCantidadActual(), "Inventario inicial", null, null, null);
        }
        
        return guardado;
    }
    
    @Transactional
    public Optional<Insumo> actualizarInsumo(Long id, InsumoDTO dto) {
        return insumoRepository.findById(id).map(insumo -> {
            insumo.setNombre(dto.getNombre());
            insumo.setDescripcion(dto.getDescripcion());
            insumo.setUnidadMedida(dto.getUnidadMedida());
            insumo.setCantidadMinima(dto.getCantidadMinima());
            insumo.setPrecioUnitario(dto.getPrecioUnitario());
            return insumoRepository.save(insumo);
        });
    }
    
    @Transactional
    public Optional<Insumo> ajustarCantidad(Long id, BigDecimal cantidad, String motivo, Long usuarioId) {
        return insumoRepository.findById(id).map(insumo -> {
            BigDecimal cantidadAnterior = insumo.getCantidadActual();
            BigDecimal cantidadNueva = cantidadAnterior.add(cantidad);
            
            if (cantidadNueva.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("La cantidad no puede ser negativa");
            }
            
            insumo.setCantidadActual(cantidadNueva);
            Insumo guardado = insumoRepository.save(insumo);
            
            String tipoMovimiento = cantidad.compareTo(BigDecimal.ZERO) > 0 ? "ENTRADA" : "SALIDA";
            registrarMovimiento(id, tipoMovimiento, cantidad.abs(), cantidadAnterior, 
                cantidadNueva, motivo, null, null, usuarioId);
            
            return guardado;
        });
    }
    
    @Transactional
    public void descontarPorVenta(Long platoId, Integer cantidad, Long ventaId) {
        List<Receta> recetas = recetaRepository.findByPlatoId(platoId);
        
        for (Receta receta : recetas) {
            Insumo insumo = insumoRepository.findById(receta.getInsumoId())
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + receta.getInsumoId()));
            
            BigDecimal cantidadADescontar = receta.getCantidadNecesaria()
                .multiply(BigDecimal.valueOf(cantidad));
            
            BigDecimal cantidadAnterior = insumo.getCantidadActual();
            BigDecimal cantidadNueva = cantidadAnterior.subtract(cantidadADescontar);
            
            if (cantidadNueva.compareTo(BigDecimal.ZERO) < 0) {
                // Permitir negativo pero registrar alerta
                System.out.println("ALERTA: Stock negativo para " + insumo.getNombre());
            }
            
            insumo.setCantidadActual(cantidadNueva);
            insumoRepository.save(insumo);
            
            registrarMovimiento(insumo.getId(), "SALIDA", cantidadADescontar, 
                cantidadAnterior, cantidadNueva, "Venta", ventaId, "VENTA", null);
        }
    }

    public void eliminarInsumo(Long id) {
        insumoRepository.deleteById(id);
    }
    
    private void registrarMovimiento(Long insumoId, String tipo, BigDecimal cantidad,
                                     BigDecimal cantidadAnterior, BigDecimal cantidadNueva,
                                     String motivo, Long referenciaId, String referenciaTipo, Long usuarioId) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumoId(insumoId);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setCantidadAnterior(cantidadAnterior);
        movimiento.setCantidadNueva(cantidadNueva);
        movimiento.setMotivo(motivo);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setUsuarioId(usuarioId);
        movimientoRepository.save(movimiento);
    }
    
    private InsumoDTO toDTO(Insumo insumo) {
        InsumoDTO dto = new InsumoDTO();
        dto.setId(insumo.getId());
        dto.setNombre(insumo.getNombre());
        dto.setDescripcion(insumo.getDescripcion());
        dto.setUnidadMedida(insumo.getUnidadMedida());
        dto.setCantidadActual(insumo.getCantidadActual());
        dto.setCantidadMinima(insumo.getCantidadMinima());
        dto.setPrecioUnitario(insumo.getPrecioUnitario());
        dto.setActivo(insumo.getActivo());
        dto.setBajoStock(insumo.isBajoStock());
        return dto;
    }
}
