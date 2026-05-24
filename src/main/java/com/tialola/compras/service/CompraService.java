package com.tialola.compras.service;

import com.tialola.compras.dto.CompraDTO;
import com.tialola.compras.dto.CompraDetalleDTO;
import com.tialola.compras.model.Compra;
import com.tialola.compras.model.CompraDetalle;
import com.tialola.compras.repository.CompraRepository;
import com.tialola.compras.repository.ProveedorRepository;
import com.tialola.inventario.repository.InsumoRepository;
import com.tialola.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {
    
    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioService inventarioService;
    
    public List<CompraDTO> obtenerTodas() {
        return compraRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<CompraDTO> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return compraRepository.findByFechaBetween(inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public CompraDTO registrarCompra(CompraDTO dto) {
        Compra compra = new Compra();
        compra.setProveedorId(dto.getProveedorId());
        compra.setNotas(dto.getNotas());
        compra.setRegistradoPor(dto.getRegistradoPor());
        
        // Agregar detalles y calcular total
        java.math.BigDecimal totalCalculado = java.math.BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (CompraDetalleDTO detalleDTO : dto.getDetalles()) {
                CompraDetalle detalle = new CompraDetalle();
                detalle.setInsumoId(detalleDTO.getInsumoId());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                
                // Calcular subtotal si no viene en el DTO
                java.math.BigDecimal subtotal = detalleDTO.getSubtotal();
                if (subtotal == null && detalleDTO.getPrecioUnitario() != null && detalleDTO.getCantidad() != null) {
                    subtotal = detalleDTO.getPrecioUnitario().multiply(detalleDTO.getCantidad());
                }
                if (subtotal == null) {
                    subtotal = java.math.BigDecimal.ZERO;
                }
                detalle.setSubtotal(subtotal);
                
                compra.addDetalle(detalle);
                totalCalculado = totalCalculado.add(subtotal);
            }
        }
        
        // Establecer el total calculado (o el del DTO si existe)
        compra.setTotal(dto.getTotal() != null ? dto.getTotal() : totalCalculado);
        
        Compra guardada = compraRepository.save(compra);
        
        // Actualizar inventario automáticamente
        if (guardada.getDetalles() != null) {
            guardada.getDetalles().forEach(detalle -> {
                try {
                    inventarioService.ajustarCantidad(
                        detalle.getInsumoId(),
                        detalle.getCantidad(),
                        "Compra #" + guardada.getId(),
                        guardada.getRegistradoPor()
                    );
                } catch (Exception e) {
                    System.err.println("Error al actualizar inventario: " + e.getMessage());
                }
            });
        }
        
        return toDTO(guardada);
    }
    
    private CompraDTO toDTO(Compra compra) {
        CompraDTO dto = new CompraDTO();
        dto.setId(compra.getId());
        dto.setProveedorId(compra.getProveedorId());
        dto.setFecha(compra.getFecha());
        dto.setTotal(compra.getTotal());
        dto.setEstado(compra.getEstado());
        dto.setNotas(compra.getNotas());
        dto.setRegistradoPor(compra.getRegistradoPor());
        
        // Obtener nombre del proveedor
        if (compra.getProveedorId() != null) {
            proveedorRepository.findById(compra.getProveedorId())
                .ifPresent(p -> dto.setProveedorNombre(p.getNombre()));
        }
        
        // Mapear detalles
        if (compra.getDetalles() != null) {
            dto.setDetalles(compra.getDetalles().stream()
                .map(detalle -> {
                    CompraDetalleDTO detalleDTO = new CompraDetalleDTO();
                    detalleDTO.setId(detalle.getId());
                    detalleDTO.setInsumoId(detalle.getInsumoId());
                    detalleDTO.setCantidad(detalle.getCantidad());
                    detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
                    detalleDTO.setSubtotal(detalle.getSubtotal());
                    
                    insumoRepository.findById(detalle.getInsumoId())
                        .ifPresent(i -> {
                            detalleDTO.setInsumoNombre(i.getNombre());
                            detalleDTO.setUnidadMedida(i.getUnidadMedida());
                        });
                    
                    return detalleDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
