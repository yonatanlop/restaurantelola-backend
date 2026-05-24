package com.tialola.inventario.controller;

import com.tialola.inventario.dto.RecetaDTO;
import com.tialola.inventario.model.Receta;
import com.tialola.inventario.service.RecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecetaController {
    
    private final RecetaService recetaService;
    
    @GetMapping("/plato/{platoId}")
    public ResponseEntity<List<RecetaDTO>> obtenerPorPlato(@PathVariable Long platoId) {
        return ResponseEntity.ok(recetaService.obtenerPorPlato(platoId));
    }
    
    @PostMapping("/plato/{platoId}")
    public ResponseEntity<Receta> agregarInsumo(@PathVariable Long platoId, @RequestBody RecetaDTO dto) {
        Receta receta = recetaService.agregarInsumoAPlato(platoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(receta);
    }
    
    @DeleteMapping("/plato/{platoId}/insumo/{insumoId}")
    public ResponseEntity<Void> eliminarInsumo(@PathVariable Long platoId, @PathVariable Long insumoId) {
        recetaService.eliminarInsumoDePlato(platoId, insumoId);
        return ResponseEntity.noContent().build();
    }
}
