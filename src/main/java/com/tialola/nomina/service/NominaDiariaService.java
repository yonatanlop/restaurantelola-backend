package com.tialola.nomina.service;

import com.tialola.nomina.dto.NominaDiariaDTO;
import com.tialola.nomina.dto.RegistroAsistenciaDTO;
import com.tialola.nomina.model.Empleado;
import com.tialola.nomina.model.NominaDiaria;
import com.tialola.nomina.repository.EmpleadoRepository;
import com.tialola.nomina.repository.NominaDiariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NominaDiariaService {
    
    private final NominaDiariaRepository nominaRepository;
    private final EmpleadoRepository empleadoRepository;
    
    public List<NominaDiariaDTO> obtenerPorFecha(LocalDate fecha) {
        return nominaRepository.findByFecha(fecha).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<NominaDiariaDTO> obtenerPorPeriodo(LocalDate inicio, LocalDate fin) {
        return nominaRepository.findByFechaBetween(inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<NominaDiariaDTO> obtenerPorEmpleado(Long empleadoId, LocalDate inicio, LocalDate fin) {
        return nominaRepository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<NominaDiariaDTO> obtenerPendientes() {
        return nominaRepository.findByEstado("PENDIENTE").stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public BigDecimal calcularTotalDia(LocalDate fecha) {
        BigDecimal total = nominaRepository.calcularTotalDia(fecha);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal calcularTotalPeriodo(LocalDate inicio, LocalDate fin) {
        BigDecimal total = nominaRepository.calcularTotalPeriodo(inicio, fin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional
    public List<NominaDiariaDTO> registrarAsistencia(RegistroAsistenciaDTO dto) {
        LocalDate fecha = dto.getFecha();
        Long registradoPor = dto.getRegistradoPor();
        
        // Registrar cada empleado que trabajó
        List<NominaDiaria> registros = dto.getEmpleadosIds().stream()
            .map(empleadoId -> {
                // Verificar si ya existe registro para ese día
                Optional<NominaDiaria> existente = nominaRepository
                    .findByEmpleadoIdAndFecha(empleadoId, fecha);
                
                if (existente.isPresent()) {
                    return existente.get();
                }
                
                // Obtener empleado para salario
                Empleado empleado = empleadoRepository.findById(empleadoId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + empleadoId));
                
                // Crear nuevo registro
                NominaDiaria nomina = new NominaDiaria();
                nomina.setEmpleadoId(empleadoId);
                nomina.setFecha(fecha);
                nomina.setMonto(empleado.getSalarioDiario());
                nomina.setEstado("PENDIENTE");
                nomina.setRegistradoPor(registradoPor);
                nomina.setNotas(dto.getNotas());
                
                return nominaRepository.save(nomina);
            })
            .collect(Collectors.toList());
        
        return registros.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Optional<NominaDiaria> marcarComoPagado(Long id, Long usuarioId) {
        return nominaRepository.findById(id).map(nomina -> {
            nomina.setEstado("PAGADO");
            nomina.setFechaPago(LocalDateTime.now());
            return nominaRepository.save(nomina);
        });
    }
    
    @Transactional
    public void marcarMultiplesComoPagado(List<Long> ids, Long usuarioId) {
        ids.forEach(id -> marcarComoPagado(id, usuarioId));
    }
    
    @Transactional
    public void eliminarRegistro(Long id) {
        nominaRepository.deleteById(id);
    }
    
    private NominaDiariaDTO toDTO(NominaDiaria nomina) {
        NominaDiariaDTO dto = new NominaDiariaDTO();
        dto.setId(nomina.getId());
        dto.setEmpleadoId(nomina.getEmpleadoId());
        dto.setFecha(nomina.getFecha());
        dto.setMonto(nomina.getMonto());
        dto.setEstado(nomina.getEstado());
        dto.setFechaPago(nomina.getFechaPago());
        dto.setNotas(nomina.getNotas());
        dto.setRegistradoPor(nomina.getRegistradoPor());
        
        // Obtener nombre del empleado
        empleadoRepository.findById(nomina.getEmpleadoId())
            .ifPresent(emp -> {
                dto.setEmpleadoNombre(emp.getNombreCompleto());
                dto.setEmpleadoPuesto(emp.getPuesto());
            });
        
        return dto;
    }
}
