package com.tialola.auditoria.controller;

import com.tialola.auditoria.dto.AuditoriaFiltroDTO;
import com.tialola.auditoria.model.AuditoriaLog;
import com.tialola.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<AuditoriaLog>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(auditoriaService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<AuditoriaLog>> obtenerPorEntidad(
            @PathVariable String entidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(auditoriaService.obtenerPorEntidad(entidad, entidadId));
    }

    @GetMapping("/accion/{accion}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<AuditoriaLog>> obtenerPorAccion(@PathVariable String accion) {
        return ResponseEntity.ok(auditoriaService.obtenerPorAccion(accion));
    }

    @GetMapping("/rango")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<AuditoriaLog>> obtenerPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(auditoriaService.obtenerPorRangoFechas(inicio, fin));
    }

    @PostMapping("/buscar")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<AuditoriaLog>> buscarConFiltros(@RequestBody AuditoriaFiltroDTO filtro) {
        return ResponseEntity.ok(auditoriaService.buscarConFiltros(filtro));
    }
}
