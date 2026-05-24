package com.tialola.mesas.service;

import com.tialola.mesas.dto.ComandaDTO;
import com.tialola.mesas.model.Comanda;
import com.tialola.mesas.model.ComandaDetalle;
import com.tialola.mesas.model.Mesa;
import com.tialola.mesas.repository.ComandaRepository;
import com.tialola.mesas.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final MesaRepository mesaRepository;

    @Transactional
    public Comanda crearComanda(ComandaDTO dto) {
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        Comanda comanda = new Comanda();
        comanda.setMesaId(dto.getMesaId());
        comanda.setUsuarioId(dto.getUsuarioId());
        comanda.setNotas(dto.getNotas());
        comanda.setEstado("PENDIENTE");
        
        // Agregar detalles
        for (ComandaDTO.ItemComanda item : dto.getItems()) {
            ComandaDetalle detalle = new ComandaDetalle();
            detalle.setPlatoId(item.getPlatoId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setNotas(item.getNotas());
            detalle.setEstado("PENDIENTE");
            
            comanda.addDetalle(detalle);
        }
        
        return comandaRepository.save(comanda);
    }

    public List<Comanda> obtenerComandasPorMesa(Long mesaId) {
        return comandaRepository.findByMesaIdOrderByFechaCreacionDesc(mesaId);
    }

    public List<Comanda> obtenerComandasPorEstado(String estado) {
        return comandaRepository.findByEstadoOrderByFechaCreacionAsc(estado);
    }

    public List<Comanda> obtenerComandasPendientes() {
        return comandaRepository.findByEstadoOrderByFechaCreacionAsc("PENDIENTE");
    }

    @Transactional
    public Comanda cambiarEstadoComanda(Long comandaId, String nuevoEstado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada"));
        
        comanda.setEstado(nuevoEstado);
        
        switch (nuevoEstado) {
            case "EN_PREPARACION":
                comanda.setFechaPreparacion(LocalDateTime.now());
                break;
            case "LISTA":
                comanda.setFechaLista(LocalDateTime.now());
                break;
            case "SERVIDA":
                comanda.setFechaServida(LocalDateTime.now());
                break;
        }
        
        return comandaRepository.save(comanda);
    }

    @Transactional
    public void cambiarEstadoItem(Long comandaId, Long detalleId, String nuevoEstado) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada"));
        
        ComandaDetalle detalle = comanda.getDetalles().stream()
                .filter(d -> d.getId().equals(detalleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
        
        detalle.setEstado(nuevoEstado);
        comandaRepository.save(comanda);
    }

    @Transactional
    public void cancelarComanda(Long comandaId, String motivo) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada"));
        
        comanda.setEstado("CANCELADA");
        comanda.setNotas(comanda.getNotas() + "\nCANCELADA: " + motivo);
        
        comandaRepository.save(comanda);
    }
}
