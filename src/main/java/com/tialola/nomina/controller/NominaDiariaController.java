package com.tialola.nomina.controller;

import com.tialola.nomina.dto.NominaDiariaDTO;
import com.tialola.nomina.dto.RegistroAsistenciaDTO;
import com.tialola.nomina.model.NominaDiaria;
import com.tialola.nomina.service.NominaDiariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nomina")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NominaDiariaController {
    
    private final NominaDiariaService nominaService;
    
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<NominaDiariaDTO>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(nominaService.obtenerPorFecha(fecha));
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<NominaDiariaDTO>> obtenerPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(nominaService.obtenerPorPeriodo(inicio, fin));
    }
    
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<NominaDiariaDTO>> obtenerPorEmpleado(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(nominaService.obtenerPorEmpleado(empleadoId, inicio, fin));
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<NominaDiariaDTO>> obtenerPendientes() {
        return ResponseEntity.ok(nominaService.obtenerPendientes());
    }
    
    @GetMapping("/total/dia/{fecha}")
    public ResponseEntity<Map<String, BigDecimal>> calcularTotalDia(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        BigDecimal total = nominaService.calcularTotalDia(fecha);
        return ResponseEntity.ok(Map.of("total", total));
    }
    
    @GetMapping("/total/periodo")
    public ResponseEntity<Map<String, BigDecimal>> calcularTotalPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        BigDecimal total = nominaService.calcularTotalPeriodo(inicio, fin);
        return ResponseEntity.ok(Map.of("total", total));
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<List<NominaDiariaDTO>> registrarAsistencia(@RequestBody RegistroAsistenciaDTO dto) {
        List<NominaDiariaDTO> registros = nominaService.registrarAsistencia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registros);
    }
    
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<NominaDiaria> marcarComoPagado(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long usuarioId = body.get("usuarioId");
        return nominaService.marcarComoPagado(id, usuarioId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/pagar-multiples")
    public ResponseEntity<Void> marcarMultiplesComoPagado(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Object> idsRaw = (List<Object>) body.get("ids");
        List<Long> ids = idsRaw.stream()
            .map(value -> Long.valueOf(value.toString()))
            .collect(Collectors.toList());
        Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
        nominaService.marcarMultiplesComoPagado(ids, usuarioId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(@PathVariable Long id) {
        nominaService.eliminarRegistro(id);
        return ResponseEntity.noContent().build();
    }
}
