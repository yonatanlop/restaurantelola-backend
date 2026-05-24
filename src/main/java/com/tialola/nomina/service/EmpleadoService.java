package com.tialola.nomina.service;

import com.tialola.nomina.dto.EmpleadoDTO;
import com.tialola.nomina.model.Empleado;
import com.tialola.nomina.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;
    
    public List<EmpleadoDTO> obtenerTodos() {
        return empleadoRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<EmpleadoDTO> obtenerActivos() {
        return empleadoRepository.findByActivoTrue().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<Empleado> obtenerPorId(Long id) {
        return empleadoRepository.findById(id);
    }
    
    public Optional<Empleado> obtenerPorDocumento(String documento) {
        return empleadoRepository.findByDocumento(documento);
    }
    
    @Transactional
    public Empleado crearEmpleado(EmpleadoDTO dto) {
        // Validar que no exista documento duplicado
        if (dto.getDocumento() != null && !dto.getDocumento().isEmpty()) {
            Optional<Empleado> existente = empleadoRepository.findByDocumento(dto.getDocumento());
            if (existente.isPresent()) {
                throw new RuntimeException("Ya existe un empleado con ese documento");
            }
        }
        
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setDocumento(dto.getDocumento());
        empleado.setTelefono(dto.getTelefono());
        empleado.setDireccion(dto.getDireccion());
        empleado.setPuesto(dto.getPuesto());
        empleado.setSalarioDiario(dto.getSalarioDiario());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setActivo(true);
        
        return empleadoRepository.save(empleado);
    }
    
    @Transactional
    public Optional<Empleado> actualizarEmpleado(Long id, EmpleadoDTO dto) {
        return empleadoRepository.findById(id).map(empleado -> {
            // Validar documento si cambió
            if (dto.getDocumento() != null && !dto.getDocumento().equals(empleado.getDocumento())) {
                Optional<Empleado> existente = empleadoRepository.findByDocumento(dto.getDocumento());
                if (existente.isPresent()) {
                    throw new RuntimeException("Ya existe un empleado con ese documento");
                }
            }
            
            empleado.setNombre(dto.getNombre());
            empleado.setApellido(dto.getApellido());
            empleado.setDocumento(dto.getDocumento());
            empleado.setTelefono(dto.getTelefono());
            empleado.setDireccion(dto.getDireccion());
            empleado.setPuesto(dto.getPuesto());
            empleado.setSalarioDiario(dto.getSalarioDiario());
            empleado.setFechaIngreso(dto.getFechaIngreso());
            
            return empleadoRepository.save(empleado);
        });
    }
    
    @Transactional
    public Optional<Empleado> cambiarEstado(Long id, Boolean activo) {
        return empleadoRepository.findById(id).map(empleado -> {
            empleado.setActivo(activo);
            return empleadoRepository.save(empleado);
        });
    }
    
    @Transactional
    public void eliminarEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }
    
    private EmpleadoDTO toDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setNombreCompleto(empleado.getNombreCompleto());
        dto.setDocumento(empleado.getDocumento());
        dto.setTelefono(empleado.getTelefono());
        dto.setDireccion(empleado.getDireccion());
        dto.setPuesto(empleado.getPuesto());
        dto.setSalarioDiario(empleado.getSalarioDiario());
        dto.setActivo(empleado.getActivo());
        dto.setFechaIngreso(empleado.getFechaIngreso());
        return dto;
    }
}
