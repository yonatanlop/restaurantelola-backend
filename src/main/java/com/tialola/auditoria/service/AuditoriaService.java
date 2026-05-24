package com.tialola.auditoria.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tialola.auditoria.dto.AuditoriaFiltroDTO;
import com.tialola.auditoria.model.AuditoriaLog;
import com.tialola.auditoria.repository.AuditoriaLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void registrarAccion(String accion, String entidad, Long entidadId, 
                                Long usuarioId, String usuarioNombre, String descripcion) {
        try {
            AuditoriaLog log = new AuditoriaLog();
            log.setAccion(accion);
            log.setEntidad(entidad);
            log.setEntidadId(entidadId);
            log.setUsuarioId(usuarioId);
            log.setUsuarioNombre(usuarioNombre);
            log.setDescripcion(descripcion);
            log.setResultado("EXITOSO");
            
            auditoriaRepository.save(log);
            this.log.info("Auditoría registrada: {} - {} - {}", accion, entidad, entidadId);
        } catch (Exception e) {
            log.error("Error al registrar auditoría: {}", e.getMessage());
        }
    }

    @Transactional
    public void registrarCambio(String accion, String entidad, Long entidadId,
                               Long usuarioId, String usuarioNombre,
                               Object datosAnteriores, Object datosNuevos, String descripcion) {
        try {
            AuditoriaLog log = new AuditoriaLog();
            log.setAccion(accion);
            log.setEntidad(entidad);
            log.setEntidadId(entidadId);
            log.setUsuarioId(usuarioId);
            log.setUsuarioNombre(usuarioNombre);
            log.setDescripcion(descripcion);
            log.setResultado("EXITOSO");
            
            if (datosAnteriores != null) {
                log.setDatosAnteriores(objectMapper.writeValueAsString(datosAnteriores));
            }
            if (datosNuevos != null) {
                log.setDatosNuevos(objectMapper.writeValueAsString(datosNuevos));
            }
            
            auditoriaRepository.save(log);
            this.log.info("Cambio auditado: {} - {} - {}", accion, entidad, entidadId);
        } catch (Exception e) {
            log.error("Error al registrar cambio en auditoría: {}", e.getMessage());
        }
    }

    @Transactional
    public void registrarError(String accion, String entidad, Long entidadId,
                              Long usuarioId, String usuarioNombre, String error) {
        try {
            AuditoriaLog log = new AuditoriaLog();
            log.setAccion(accion);
            log.setEntidad(entidad);
            log.setEntidadId(entidadId);
            log.setUsuarioId(usuarioId);
            log.setUsuarioNombre(usuarioNombre);
            log.setResultado("FALLIDO");
            log.setMensajeError(error);
            
            auditoriaRepository.save(log);
            this.log.warn("Error auditado: {} - {} - {}", accion, entidad, error);
        } catch (Exception e) {
            log.error("Error al registrar error en auditoría: {}", e.getMessage());
        }
    }

    public List<AuditoriaLog> obtenerPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public List<AuditoriaLog> obtenerPorEntidad(String entidad, Long entidadId) {
        return auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaDesc(entidad, entidadId);
    }

    public List<AuditoriaLog> obtenerPorAccion(String accion) {
        return auditoriaRepository.findByAccionOrderByFechaDesc(accion);
    }

    public List<AuditoriaLog> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
    }

    public List<AuditoriaLog> buscarConFiltros(AuditoriaFiltroDTO filtro) {
        if (filtro.getEntidad() != null && filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            return auditoriaRepository.findByFechaAndEntidad(
                filtro.getFechaInicio(), 
                filtro.getFechaFin(), 
                filtro.getEntidad()
            );
        } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            return auditoriaRepository.findByFechaBetweenOrderByFechaDesc(
                filtro.getFechaInicio(), 
                filtro.getFechaFin()
            );
        } else if (filtro.getUsuarioId() != null) {
            return auditoriaRepository.findByUsuarioIdOrderByFechaDesc(filtro.getUsuarioId());
        } else if (filtro.getAccion() != null) {
            return auditoriaRepository.findByAccionOrderByFechaDesc(filtro.getAccion());
        } else if (filtro.getEntidad() != null && filtro.getEntidadId() != null) {
            return auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaDesc(
                filtro.getEntidad(), 
                filtro.getEntidadId()
            );
        }
        
        return auditoriaRepository.findAll();
    }
}
