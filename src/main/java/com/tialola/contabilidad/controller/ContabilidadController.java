package com.tialola.contabilidad.controller;

import com.tialola.contabilidad.dto.MovimientoCajaDTO;
import com.tialola.contabilidad.dto.ResumenCajaDTO;
import com.tialola.contabilidad.model.MovimientoCaja;
import com.tialola.contabilidad.service.ContabilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contabilidad")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContabilidadController {
    
    private final ContabilidadService contabilidadService;
    
    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoCajaDTO>> obtenerMovimientos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(contabilidadService.obtenerMovimientos(inicio, fin));
    }
    
    @GetMapping("/movimientos/tipo/{tipo}")
    public ResponseEntity<List<MovimientoCajaDTO>> obtenerPorTipo(
            @PathVariable String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(contabilidadService.obtenerPorTipo(tipo, inicio, fin));
    }
    
    @GetMapping("/movimientos/concepto/{concepto}")
    public ResponseEntity<List<MovimientoCajaDTO>> obtenerPorConcepto(
            @PathVariable String concepto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(contabilidadService.obtenerPorConcepto(concepto, inicio, fin));
    }
    
    @GetMapping("/resumen")
    public ResponseEntity<ResumenCajaDTO> obtenerResumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(contabilidadService.obtenerResumen(inicio, fin));
    }
    
    @PostMapping("/movimiento")
    public ResponseEntity<MovimientoCaja> registrarMovimiento(@RequestBody Map<String, Object> body) {
        String tipo = (String) body.get("tipo");
        String concepto = (String) body.get("concepto");
        String descripcion = (String) body.get("descripcion");
        BigDecimal monto = new BigDecimal(body.get("monto").toString());
        Long referenciaId = body.get("referenciaId") != null ? 
            Long.valueOf(body.get("referenciaId").toString()) : null;
        String referenciaTipo = (String) body.get("referenciaTipo");
        Long usuarioId = body.get("usuarioId") != null ? 
            Long.valueOf(body.get("usuarioId").toString()) : null;
        
        MovimientoCaja movimiento = contabilidadService.registrarMovimiento(
            tipo, concepto, descripcion, monto, referenciaId, referenciaTipo, usuarioId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
}
