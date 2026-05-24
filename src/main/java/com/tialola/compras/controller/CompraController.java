package com.tialola.compras.controller;

import com.tialola.compras.dto.CompraDTO;
import com.tialola.compras.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompraController {
    
    private final CompraService compraService;
    
    @GetMapping
    public ResponseEntity<List<CompraDTO>> obtenerTodas() {
        return ResponseEntity.ok(compraService.obtenerTodas());
    }
    
    @GetMapping("/fecha")
    public ResponseEntity<List<CompraDTO>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(compraService.obtenerPorFecha(inicio, fin));
    }
    
    @PostMapping
    public ResponseEntity<CompraDTO> registrarCompra(@RequestBody CompraDTO dto) {
        CompraDTO compra = compraService.registrarCompra(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }
}
