package com.tialola.contabilidad.ventas.service;

import com.tialola.auth.model.Usuario;
import com.tialola.auth.repository.UsuarioRepository;
import com.tialola.contabilidad.ventas.dto.VentaDTO;
import com.tialola.contabilidad.ventas.mapper.VentaMapper;
import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.model.VentaDetalle;
import com.tialola.contabilidad.ventas.service.PropinaVentaService;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import com.tialola.inventario.service.InventarioService;
import com.tialola.menu.model.Plato;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final UsuarioRepository usuarioRepository;
    private final PlatoRepository platoRepository;
    private final InventarioService inventarioService;
    private final PropinaVentaService propinaVentaService;
    private final com.tialola.caja.service.CajaRegistradoraService cajaRegistradoraService;
    
    @Transactional
    public VentaDTO crearVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setUsuarioId(ventaDTO.getUsuarioId());
        venta.setSubtotal(ventaDTO.getSubtotal());
        venta.setImpuestos(ventaDTO.getImpuestos());
        venta.setPropina(ventaDTO.getPropina());
        venta.setTotal(ventaDTO.getTotal());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setNotas(ventaDTO.getNotas());
        
        // Agregar detalles
        if (ventaDTO.getDetalles() != null) {
            ventaDTO.getDetalles().forEach(detalleDTO -> {
                VentaDetalle detalle = new VentaDetalle();
                detalle.setPlatoId(detalleDTO.getPlatoId());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                detalle.setSubtotal(detalleDTO.getSubtotal());
                detalle.setNotas(detalleDTO.getNotas());
                venta.addDetalle(detalle);
            });
        }
        
        Venta guardada = ventaRepository.save(venta);

        // Preparar estructura de propinas (HU-014)
        propinaVentaService.prepararRegistroParaVenta(guardada);
        
        // Descontar inventario automáticamente (HU-003)
        if (guardada.getDetalles() != null) {
            guardada.getDetalles().forEach(detalle -> {
                try {
                    inventarioService.descontarPorVenta(
                        detalle.getPlatoId(), 
                        detalle.getCantidad(), 
                        guardada.getId()
                    );
                } catch (Exception e) {
                    System.err.println("Error al descontar inventario: " + e.getMessage());
                    // Continuar con la venta aunque falle el descuento
                }
            });
        }
        
        // Abrir cajón de caja registradora automáticamente
        try {
            cajaRegistradoraService.abrirCajon();
        } catch (Exception e) {
            System.err.println("Error al abrir cajón: " + e.getMessage());
            // Continuar con la venta aunque falle la apertura del cajón
        }
        
        return ventaMapper.toDTO(guardada);
    }
    
    public List<VentaDTO> obtenerVentasDelDia() {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finDia = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return ventaRepository.findByFechaBetween(inicioDia, finDia)
            .stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<VentaDTO> obtenerTodasLasVentas() {
        return ventaRepository.findAll()
            .stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
}
