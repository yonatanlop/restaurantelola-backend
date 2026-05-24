package com.tialola.impresion.controller;

import com.tialola.impresion.model.ColaImpresion;
import com.tialola.impresion.model.ConfiguracionImpresora;
import com.tialola.impresion.repository.ConfiguracionImpresoraRepository;
import com.tialola.impresion.service.ImpresionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/impresion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImpresionController {

    private final ImpresionService impresionService;
    private final ConfiguracionImpresoraRepository configuracionRepository;

    @PostMapping("/ticket/{ventaId}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> imprimirTicket(@PathVariable Long ventaId) {
        impresionService.encolarTicketVenta(ventaId);
        impresionService.procesarColaPendiente();
        return ResponseEntity.ok(Map.of("mensaje", "Ticket encolado para impresión"));
    }

    @PostMapping("/comanda/{ventaId}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> imprimirComanda(@PathVariable Long ventaId) {
        impresionService.encolarComanda(ventaId);
        impresionService.procesarColaPendiente();
        return ResponseEntity.ok(Map.of("mensaje", "Comanda encolada para impresión"));
    }

    @GetMapping("/cola")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<ColaImpresion>> obtenerColaPendiente() {
        return ResponseEntity.ok(impresionService.obtenerColaPendiente());
    }

    @PostMapping("/cola/{colaId}/reintentar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> reintentarImpresion(@PathVariable Long colaId) {
        impresionService.reintentarImpresion(colaId);
        return ResponseEntity.ok(Map.of("mensaje", "Reintentando impresión"));
    }

    @PostMapping("/procesar-cola")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> procesarCola() {
        impresionService.procesarColaPendiente();
        return ResponseEntity.ok(Map.of("mensaje", "Cola procesada"));
    }

    @GetMapping("/configuracion")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<ConfiguracionImpresora>> obtenerConfiguraciones() {
        return ResponseEntity.ok(configuracionRepository.findByActivoTrue());
    }

    @PostMapping("/configuracion")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<ConfiguracionImpresora> crearConfiguracion(
            @RequestBody ConfiguracionImpresora config) {
        return ResponseEntity.ok(configuracionRepository.save(config));
    }

    @PutMapping("/configuracion/{id}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<ConfiguracionImpresora> actualizarConfiguracion(
            @PathVariable Long id,
            @RequestBody ConfiguracionImpresora config) {
        config.setId(id);
        return ResponseEntity.ok(configuracionRepository.save(config));
    }
}
