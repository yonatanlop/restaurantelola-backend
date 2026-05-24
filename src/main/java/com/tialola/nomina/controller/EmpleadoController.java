package com.tialola.nomina.controller;

import com.tialola.nomina.dto.EmpleadoDTO;
import com.tialola.nomina.model.Empleado;
import com.tialola.nomina.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {
    
    private final EmpleadoService empleadoService;
    
    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerTodos() {
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoDTO>> obtenerActivos() {
        return ResponseEntity.ok(empleadoService.obtenerActivos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable Long id) {
        return empleadoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/documento/{documento}")
    public ResponseEntity<Empleado> obtenerPorDocumento(@PathVariable String documento) {
        return empleadoService.obtenerPorDocumento(documento)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crearEmpleado(@RequestBody EmpleadoDTO dto) {
        try {
            Empleado empleado = empleadoService.crearEmpleado(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        try {
            return empleadoService.actualizarEmpleado(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Empleado> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean activo = body.get("activo");
        return empleadoService.cambiarEstado(id, activo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }
}
