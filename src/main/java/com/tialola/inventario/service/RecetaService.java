package com.tialola.inventario.service;

import com.tialola.inventario.dto.RecetaDTO;
import com.tialola.inventario.model.Insumo;
import com.tialola.inventario.model.Receta;
import com.tialola.inventario.repository.InsumoRepository;
import com.tialola.inventario.repository.RecetaRepository;
import com.tialola.menu.model.Plato;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {
    
    private final RecetaRepository recetaRepository;
    private final PlatoRepository platoRepository;
    private final InsumoRepository insumoRepository;
    
    public List<RecetaDTO> obtenerPorPlato(Long platoId) {
        return recetaRepository.findByPlatoId(platoId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Receta agregarInsumoAPlato(Long platoId, RecetaDTO dto) {
        Receta receta = new Receta();
        receta.setPlatoId(platoId);
        receta.setInsumoId(dto.getInsumoId());
        receta.setCantidadNecesaria(dto.getCantidadNecesaria());
        return recetaRepository.save(receta);
    }
    
    @Transactional
    public void eliminarInsumoDePlato(Long platoId, Long insumoId) {
        recetaRepository.deleteByPlatoIdAndInsumoId(platoId, insumoId);
    }
    
    private RecetaDTO toDTO(Receta receta) {
        RecetaDTO dto = new RecetaDTO();
        dto.setId(receta.getId());
        dto.setPlatoId(receta.getPlatoId());
        dto.setInsumoId(receta.getInsumoId());
        dto.setCantidadNecesaria(receta.getCantidadNecesaria());
        
        platoRepository.findById(receta.getPlatoId())
            .ifPresent(plato -> dto.setPlatoNombre(plato.getNombre()));
        
        insumoRepository.findById(receta.getInsumoId())
            .ifPresent(insumo -> {
                dto.setInsumoNombre(insumo.getNombre());
                dto.setUnidadMedida(insumo.getUnidadMedida());
            });
        
        return dto;
    }
}
