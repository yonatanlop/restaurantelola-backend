package com.tialola.mesas.service;

import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import com.tialola.mesas.dto.MesaDTO;
import com.tialola.mesas.dto.TransferenciaMesaDTO;
import com.tialola.mesas.model.Mesa;
import com.tialola.mesas.model.TransferenciaMesa;
import com.tialola.mesas.repository.MesaRepository;
import com.tialola.mesas.repository.TransferenciaMesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MesaService {

    private final MesaRepository mesaRepository;
    private final VentaRepository ventaRepository;
    private final TransferenciaMesaRepository transferenciaRepository;

    public List<MesaDTO> obtenerTodasLasMesas() {
        return mesaRepository.findByActivaTrueOrderByNumeroAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<MesaDTO> obtenerMesasPorEstado(String estado) {
        return mesaRepository.findByEstadoAndActivaTrue(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<MesaDTO> obtenerMesasPorUbicacion(String ubicacion) {
        return mesaRepository.findByUbicacionAndActivaTrue(ubicacion).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Mesa crearMesa(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    @Transactional
    public Mesa actualizarMesa(Long id, Mesa mesaActualizada) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        mesa.setNumero(mesaActualizada.getNumero());
        mesa.setCapacidad(mesaActualizada.getCapacidad());
        mesa.setUbicacion(mesaActualizada.getUbicacion());
        
        return mesaRepository.save(mesa);
    }

    @Transactional
    public void ocuparMesa(Long mesaId, Long ventaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        if (!"LIBRE".equals(mesa.getEstado())) {
            throw new RuntimeException("La mesa no está disponible");
        }
        
        mesa.setEstado("OCUPADA");
        mesa.setVentaActualId(ventaId);
        mesa.setHoraOcupacion(LocalDateTime.now());
        
        mesaRepository.save(mesa);
    }

    @Transactional
    public void liberarMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        mesa.setEstado("LIBRE");
        mesa.setVentaActualId(null);
        mesa.setHoraOcupacion(null);
        
        mesaRepository.save(mesa);
    }

    @Transactional
    public void transferirMesa(TransferenciaMesaDTO dto) {
        Mesa mesaOrigen = mesaRepository.findById(dto.getMesaOrigenId())
                .orElseThrow(() -> new RuntimeException("Mesa origen no encontrada"));
        
        Mesa mesaDestino = mesaRepository.findById(dto.getMesaDestinoId())
                .orElseThrow(() -> new RuntimeException("Mesa destino no encontrada"));
        
        if (!"LIBRE".equals(mesaDestino.getEstado())) {
            throw new RuntimeException("La mesa destino no está disponible");
        }
        
        // Transferir la venta
        mesaDestino.setEstado("OCUPADA");
        mesaDestino.setVentaActualId(mesaOrigen.getVentaActualId());
        mesaDestino.setHoraOcupacion(mesaOrigen.getHoraOcupacion());
        
        mesaOrigen.setEstado("LIBRE");
        mesaOrigen.setVentaActualId(null);
        mesaOrigen.setHoraOcupacion(null);
        
        mesaRepository.save(mesaOrigen);
        mesaRepository.save(mesaDestino);
        
        // Registrar la transferencia
        TransferenciaMesa transferencia = new TransferenciaMesa();
        transferencia.setMesaOrigenId(dto.getMesaOrigenId());
        transferencia.setMesaDestinoId(dto.getMesaDestinoId());
        transferencia.setVentaId(dto.getVentaId());
        transferencia.setUsuarioId(dto.getUsuarioId());
        transferencia.setMotivo(dto.getMotivo());
        
        transferenciaRepository.save(transferencia);
    }

    @Transactional
    public void cambiarEstadoMesa(Long mesaId, String nuevoEstado) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        mesa.setEstado(nuevoEstado);
        mesaRepository.save(mesa);
    }

    private MesaDTO convertirADTO(Mesa mesa) {
        MesaDTO dto = MesaDTO.builder()
                .id(mesa.getId())
                .numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad())
                .ubicacion(mesa.getUbicacion())
                .estado(mesa.getEstado())
                .ventaActualId(mesa.getVentaActualId())
                .horaOcupacion(mesa.getHoraOcupacion())
                .build();
        
        // Calcular tiempo de ocupación
        if (mesa.getHoraOcupacion() != null) {
            Duration duracion = Duration.between(mesa.getHoraOcupacion(), LocalDateTime.now());
            dto.setTiempoOcupacion((int) duracion.toMinutes());
        }
        
        // Obtener total de la cuenta
        if (mesa.getVentaActualId() != null) {
            ventaRepository.findById(mesa.getVentaActualId())
                    .ifPresent(venta -> dto.setTotalCuenta(venta.getTotal()));
        }
        
        return dto;
    }
}
