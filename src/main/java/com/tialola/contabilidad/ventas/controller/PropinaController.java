package com.tialola.contabilidad.ventas.controller;

import com.tialola.contabilidad.ventas.dto.PropinaEstadoDTO;
import com.tialola.contabilidad.ventas.service.PropinaVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/propinas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PropinaController {

    private final PropinaVentaService propinaVentaService;

    @GetMapping("/estado")
    public ResponseEntity<PropinaEstadoDTO> obtenerEstado() {
        return ResponseEntity.ok(propinaVentaService.obtenerEstado());
    }
}

