package com.tialola.caja.controller;

import com.tialola.caja.dto.CierreCajaAperturaDTO;
import com.tialola.caja.dto.CierreCajaDTO;
import com.tialola.caja.dto.CierreCajaDetalleDTO;
import com.tialola.caja.dto.CierreCajaEstadoDTO;
import com.tialola.caja.dto.CierreCajaReporteDTO;
import com.tialola.caja.service.CierreCajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cierre-caja")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CierreCajaController {

    private final CierreCajaService cierreCajaService;

    @GetMapping("/estado")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<CierreCajaEstadoDTO> obtenerEstado() {
        return ResponseEntity.ok(cierreCajaService.obtenerEstadoCierre());
    }

    @PostMapping("/iniciar")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<?> iniciar(@RequestBody CierreCajaAperturaDTO dto) {
        try {
            return ResponseEntity.ok(cierreCajaService.iniciarCierre(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(
                java.util.Map.of("message", e.getMessage(), "error", e.getClass().getSimpleName())
            );
        }
    }

    @PostMapping("/{cierreId}/cerrar")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<CierreCajaDetalleDTO> cerrar(
            @PathVariable Long cierreId,
            @RequestBody CierreCajaDTO dto
    ) {
        return ResponseEntity.ok(cierreCajaService.completarCierre(cierreId, dto));
    }

    @GetMapping("/historial")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<CierreCajaDetalleDTO>> historial(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return ResponseEntity.ok(cierreCajaService.obtenerCierresPorRango(inicio, fin));
    }

    @GetMapping("/{cierreId}/reporte")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<CierreCajaReporteDTO> reporte(@PathVariable Long cierreId) {
        return ResponseEntity.ok(cierreCajaService.generarReporte(cierreId));
    }
}

