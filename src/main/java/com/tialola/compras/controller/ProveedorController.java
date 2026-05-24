package com.tialola.compras.controller;

import com.tialola.compras.dto.ProveedorDTO;
import com.tialola.compras.model.Proveedor;
import com.tialola.compras.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {
    
    private final ProveedorService proveedorService;
    
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> obtenerTodos() {
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorDTO>> obtenerActivos() {
        return ResponseEntity.ok(proveedorService.obtenerActivos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return proveedorService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = proveedorService.crearProveedor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedor);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Long id, @RequestBody ProveedorDTO dto) {
        return proveedorService.actualizarProveedor(id, dto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Proveedor> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean activo = body.get("activo");
        return proveedorService.cambiarEstado(id, activo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
