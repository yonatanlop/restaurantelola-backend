package com.tialola.nomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAsistenciaDTO {
    private LocalDate fecha;
    private List<Long> empleadosIds;
    private String notas;
    private Long registradoPor;
}
