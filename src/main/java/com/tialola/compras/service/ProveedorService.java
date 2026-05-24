package com.tialola.compras.service;

import com.tialola.compras.dto.ProveedorDTO;
import com.tialola.compras.model.Proveedor;
import com.tialola.compras.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    
    private final ProveedorRepository proveedorRepository;
    
    public List<ProveedorDTO> obtenerTodos() {
        return proveedorRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<ProveedorDTO> obtenerActivos() {
        return proveedorRepository.findByActivoTrue().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<Proveedor> obtenerPorId(Long id) {
        return proveedorRepository.findById(id);
    }
    
    @Transactional
    public Proveedor crearProveedor(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }
    
    @Transactional
    public Optional<Proveedor> actualizarProveedor(Long id, ProveedorDTO dto) {
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setNombre(dto.getNombre());
            proveedor.setContacto(dto.getContacto());
            proveedor.setTelefono(dto.getTelefono());
            proveedor.setEmail(dto.getEmail());
            proveedor.setDireccion(dto.getDireccion());
            return proveedorRepository.save(proveedor);
        });
    }
    
    @Transactional
    public Optional<Proveedor> cambiarEstado(Long id, Boolean activo) {
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setActivo(activo);
            return proveedorRepository.save(proveedor);
        });
    }
    
    private ProveedorDTO toDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setContacto(proveedor.getContacto());
        dto.setTelefono(proveedor.getTelefono());
        dto.setEmail(proveedor.getEmail());
        dto.setDireccion(proveedor.getDireccion());
        dto.setActivo(proveedor.getActivo());
        return dto;
    }
}
