package com.tialola.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaFiltroDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long usuarioId;
    private String accion;
    private String entidad;
    private Long entidadId;
}
