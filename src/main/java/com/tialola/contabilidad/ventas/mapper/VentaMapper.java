package com.tialola.contabilidad.ventas.mapper;

import com.tialola.auth.repository.UsuarioRepository;
import com.tialola.contabilidad.ventas.dto.VentaDTO;
import com.tialola.contabilidad.ventas.dto.VentaDetalleDTO;
import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VentaMapper {
    
    private final UsuarioRepository usuarioRepository;
    private final PlatoRepository platoRepository;
    
    public VentaDTO toDTO(Venta venta) {
        if (venta == null) return null;
        
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setUsuarioId(venta.getUsuarioId());
        dto.setSubtotal(venta.getSubtotal());
        dto.setImpuestos(venta.getImpuestos());
        dto.setPropina(venta.getPropina());
        dto.setTotal(venta.getTotal());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setEstado(venta.getEstado());
        dto.setNotas(venta.getNotas());
        
        // Obtener nombre del cajero
        usuarioRepository.findById(venta.getUsuarioId())
            .ifPresent(usuario -> dto.setCajero(usuario.getNombre()));
        
        // Mapear detalles
        if (venta.getDetalles() != null) {
            dto.setDetalles(venta.getDetalles().stream()
                .map(detalle -> {
                    VentaDetalleDTO detalleDTO = new VentaDetalleDTO();
                    detalleDTO.setId(detalle.getId());
                    detalleDTO.setPlatoId(detalle.getPlatoId());
                    detalleDTO.setCantidad(detalle.getCantidad());
                    detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
                    detalleDTO.setSubtotal(detalle.getSubtotal());
                    detalleDTO.setNotas(detalle.getNotas());
                    
                    // Obtener nombre del plato
                    platoRepository.findById(detalle.getPlatoId())
                        .ifPresent(plato -> detalleDTO.setNombre(plato.getNombre()));
                    
                    return detalleDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
