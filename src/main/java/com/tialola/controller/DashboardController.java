package com.tialola.controller;

import com.tialola.dto.DashboardDTO;
import com.tialola.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        DashboardDTO dashboard = dashboardService.obtenerDashboard();
        return ResponseEntity.ok(dashboard);
    }
}
