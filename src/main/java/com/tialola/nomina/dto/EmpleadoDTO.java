package com.tialola.nomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String documento;
    private String telefono;
    private String direccion;
    private String puesto;
    private BigDecimal salarioDiario;
    private Boolean activo;
    private LocalDate fechaIngreso;
}
