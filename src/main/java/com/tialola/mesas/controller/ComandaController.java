package com.tialola.mesas.controller;

import com.tialola.mesas.dto.ComandaDTO;
import com.tialola.mesas.model.Comanda;
import com.tialola.mesas.service.ComandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComandaController {

    private final ComandaService comandaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Comanda> crearComanda(@RequestBody ComandaDTO dto) {
        return ResponseEntity.ok(comandaService.crearComanda(dto));
    }

    @GetMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Comanda>> obtenerComandasPorMesa(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.obtenerComandasPorMesa(mesaId));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Comanda>> obtenerComandasPendientes() {
        return ResponseEntity.ok(comandaService.obtenerComandasPendientes());
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Comanda>> obtenerComandasPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(comandaService.obtenerComandasPorEstado(estado));
    }

    @PatchMapping("/{comandaId}/estado")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Comanda> cambiarEstadoComanda(
            @PathVariable Long comandaId,
            @RequestParam String estado) {
        return ResponseEntity.ok(comandaService.cambiarEstadoComanda(comandaId, estado));
    }

    @PatchMapping("/{comandaId}/item/{detalleId}/estado")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> cambiarEstadoItem(
            @PathVariable Long comandaId,
            @PathVariable Long detalleId,
            @RequestParam String estado) {
        comandaService.cambiarEstadoItem(comandaId, detalleId, estado);
        return ResponseEntity.ok(Map.of("mensaje", "Estado del item actualizado"));
    }

    @PostMapping("/{comandaId}/cancelar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> cancelarComanda(
            @PathVariable Long comandaId,
            @RequestParam String motivo) {
        comandaService.cancelarComanda(comandaId, motivo);
        return ResponseEntity.ok(Map.of("mensaje", "Comanda cancelada"));
    }
}
