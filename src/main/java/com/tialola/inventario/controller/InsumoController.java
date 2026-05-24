package com.tialola.inventario.controller;

import com.tialola.inventario.dto.InsumoDTO;
import com.tialola.inventario.model.Insumo;
import com.tialola.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InsumoController {
    
    private final InventarioService inventarioService;
    
    @GetMapping
    public ResponseEntity<List<InsumoDTO>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<InsumoDTO>> obtenerActivos() {
        return ResponseEntity.ok(inventarioService.obtenerActivos());
    }
    
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<InsumoDTO>> obtenerBajoStock() {
        return ResponseEntity.ok(inventarioService.obtenerBajoStock());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Insumo> obtenerPorId(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Insumo> crearInsumo(@RequestBody InsumoDTO dto) {
        Insumo insumo = inventarioService.crearInsumo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(insumo);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Insumo> actualizarInsumo(@PathVariable Long id, @RequestBody InsumoDTO dto) {
        return inventarioService.actualizarInsumo(id, dto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ajustar")
    public ResponseEntity<Insumo> ajustarCantidad(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> body) {
        BigDecimal cantidad = new BigDecimal(body.get("cantidad").toString());
        String motivo = (String) body.get("motivo");
        Long usuarioId = body.get("usuarioId") != null ? 
            Long.valueOf(body.get("usuarioId").toString()) : null;
        
        return inventarioService.ajustarCantidad(id, cantidad, motivo, usuarioId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInsumo(@PathVariable Long id) {
        inventarioService.eliminarInsumo(id);
        return ResponseEntity.noContent().build();
    }
}
