package com.tialola.menu.controller;

import com.tialola.menu.dto.PlatoDTO;
import com.tialola.menu.model.Plato;
import com.tialola.menu.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlatoController {
    
    private final PlatoService platoService;
    
    @GetMapping("/activos")
    public ResponseEntity<List<Plato>> obtenerPlatosActivos() {
        return ResponseEntity.ok(platoService.obtenerPlatosActivos());
    }
    
    @GetMapping
    public ResponseEntity<List<Plato>> obtenerTodos() {
        return ResponseEntity.ok(platoService.obtenerTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Plato> obtenerPorId(@PathVariable Long id) {
        return platoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Plato> crearPlato(@RequestBody PlatoDTO platoDTO) {
        Plato plato = platoService.crearPlato(platoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(plato);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Plato> actualizarPlato(@PathVariable Long id, @RequestBody PlatoDTO platoDTO) {
        return platoService.actualizarPlato(id, platoDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Plato> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean activo = body.get("activo");
        return platoService.cambiarEstado(id, activo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        platoService.eliminarPlato(id);
        return ResponseEntity.noContent().build();
    }
}
