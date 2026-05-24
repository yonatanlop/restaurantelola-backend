package com.tialola.contabilidad.ventas.controller;

import com.tialola.contabilidad.ventas.dto.VentaDTO;
import com.tialola.contabilidad.ventas.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {
    
    private final VentaService ventaService;
    
    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@RequestBody VentaDTO ventaDTO) {
        VentaDTO nuevaVenta = ventaService.crearVenta(ventaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }
    
    @GetMapping("/dia")
    public ResponseEntity<List<VentaDTO>> obtenerVentasDelDia() {
        return ResponseEntity.ok(ventaService.obtenerVentasDelDia());
    }
    
    @GetMapping
    public ResponseEntity<List<VentaDTO>> obtenerTodasLasVentas() {
        return ResponseEntity.ok(ventaService.obtenerTodasLasVentas());
    }
}
