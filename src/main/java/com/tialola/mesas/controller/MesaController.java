package com.tialola.mesas.controller;

import com.tialola.mesas.dto.MesaDTO;
import com.tialola.mesas.dto.TransferenciaMesaDTO;
import com.tialola.mesas.model.Mesa;
import com.tialola.mesas.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<MesaDTO>> obtenerTodasLasMesas() {
        return ResponseEntity.ok(mesaService.obtenerTodasLasMesas());
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<MesaDTO>> obtenerMesasPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(mesaService.obtenerMesasPorEstado(estado));
    }

    @GetMapping("/ubicacion/{ubicacion}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<MesaDTO>> obtenerMesasPorUbicacion(@PathVariable String ubicacion) {
        return ResponseEntity.ok(mesaService.obtenerMesasPorUbicacion(ubicacion));
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<Mesa> crearMesa(@RequestBody Mesa mesa) {
        return ResponseEntity.ok(mesaService.crearMesa(mesa));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<Mesa> actualizarMesa(@PathVariable Long id, @RequestBody Mesa mesa) {
        return ResponseEntity.ok(mesaService.actualizarMesa(id, mesa));
    }

    @PostMapping("/{mesaId}/ocupar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> ocuparMesa(
            @PathVariable Long mesaId,
            @RequestParam Long ventaId) {
        mesaService.ocuparMesa(mesaId, ventaId);
        return ResponseEntity.ok(Map.of("mensaje", "Mesa ocupada exitosamente"));
    }

    @PostMapping("/{mesaId}/liberar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> liberarMesa(@PathVariable Long mesaId) {
        mesaService.liberarMesa(mesaId);
        return ResponseEntity.ok(Map.of("mensaje", "Mesa liberada exitosamente"));
    }

    @PostMapping("/transferir")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> transferirMesa(@RequestBody TransferenciaMesaDTO dto) {
        mesaService.transferirMesa(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Mesa transferida exitosamente"));
    }

    @PatchMapping("/{mesaId}/estado")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> cambiarEstado(
            @PathVariable Long mesaId,
            @RequestParam String estado) {
        mesaService.cambiarEstadoMesa(mesaId, estado);
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
    }
}
