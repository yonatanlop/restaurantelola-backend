package com.tialola.menu.service;

import com.tialola.menu.dto.PlatoDTO;
import com.tialola.menu.model.Plato;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlatoService {
    
    private final PlatoRepository platoRepository;
    
    public List<Plato> obtenerPlatosActivos() {
        return platoRepository.findByActivoTrue();
    }
    
    public List<Plato> obtenerTodos() {
        return platoRepository.findAll();
    }
    
    public Optional<Plato> obtenerPorId(Long id) {
        return platoRepository.findById(id);
    }
    
    @Transactional
    public Plato crearPlato(PlatoDTO platoDTO) {
        Plato plato = new Plato();
        plato.setNombre(platoDTO.getNombre());
        plato.setDescripcion(platoDTO.getDescripcion());
        plato.setPrecio(platoDTO.getPrecio());
        plato.setTipoComida(platoDTO.getTipoComida());
        plato.setActivo(true);
        return platoRepository.save(plato);
    }
    
    @Transactional
    public Optional<Plato> actualizarPlato(Long id, PlatoDTO platoDTO) {
        return platoRepository.findById(id).map(plato -> {
            plato.setNombre(platoDTO.getNombre());
            plato.setDescripcion(platoDTO.getDescripcion());
            plato.setPrecio(platoDTO.getPrecio());
            plato.setTipoComida(platoDTO.getTipoComida());
            return platoRepository.save(plato);
        });
    }
    
    @Transactional
    public Optional<Plato> cambiarEstado(Long id, Boolean activo) {
        return platoRepository.findById(id).map(plato -> {
            plato.setActivo(activo);
            return platoRepository.save(plato);
        });
    }
    
    @Transactional
    public void eliminarPlato(Long id) {
        platoRepository.deleteById(id);
    }
}
