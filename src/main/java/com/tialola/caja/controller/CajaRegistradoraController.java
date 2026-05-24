package com.tialola.caja.controller;

import com.tialola.caja.service.CajaRegistradoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/caja-registradora")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CajaRegistradoraController {

    private final CajaRegistradoraService cajaRegistradoraService;

    /**
     * Endpoint para abrir el cajón de la caja registradora
     */
    @PostMapping("/abrir-cajon")
    public ResponseEntity<Map<String, Object>> abrirCajon() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean exito = cajaRegistradoraService.abrirCajon();
            
            response.put("exito", exito);
            response.put("mensaje", exito 
                ? "Cajón abierto exitosamente" 
                : "No se pudo abrir el cajón. Verifica la configuración.");
            response.put("puerto", cajaRegistradoraService.getPuerto());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("exito", false);
            response.put("mensaje", "Error al abrir cajón: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para verificar el estado de la caja registradora
     */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("habilitada", cajaRegistradoraService.isHabilitada());
        response.put("puerto", cajaRegistradoraService.getPuerto());
        
        return ResponseEntity.ok(response);
    }
}
