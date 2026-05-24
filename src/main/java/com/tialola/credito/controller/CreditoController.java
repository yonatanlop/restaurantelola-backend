package com.tialola.credito.controller;

import com.tialola.credito.dto.ClienteDTO;
import com.tialola.credito.dto.CreditoDTO;
import com.tialola.credito.model.Cliente;
import com.tialola.credito.model.Credito;
import com.tialola.credito.service.CreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/creditos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CreditoController {

    private final CreditoService creditoService;

    // Clientes
    @GetMapping("/clientes")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<ClienteDTO>> obtenerClientes() {
        return ResponseEntity.ok(creditoService.obtenerClientesActivos());
    }

    @GetMapping("/clientes/buscar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Cliente>> buscarClientes(@RequestParam String nombre) {
        return ResponseEntity.ok(creditoService.buscarClientes(nombre));
    }

    @PostMapping("/clientes")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(creditoService.crearCliente(cliente));
    }

    @PutMapping("/clientes/{id}")
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody Cliente cliente) {
        return ResponseEntity.ok(creditoService.actualizarCliente(id, cliente));
    }

    // Créditos
    @PostMapping
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Credito> registrarCredito(@RequestBody CreditoDTO dto) {
        return ResponseEntity.ok(creditoService.registrarCredito(dto));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Credito>> obtenerCreditosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(creditoService.obtenerCreditosPorCliente(clienteId));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Credito>> obtenerCreditosPendientes() {
        return ResponseEntity.ok(creditoService.obtenerCreditosPendientes());
    }

    @GetMapping("/cliente/{clienteId}/pendientes")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<List<Credito>> obtenerCreditosPendientesPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(creditoService.obtenerCreditosPendientesPorCliente(clienteId));
    }

    @GetMapping("/cliente/{clienteId}/deuda")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, BigDecimal>> obtenerDeudaCliente(@PathVariable Long clienteId) {
        BigDecimal deuda = creditoService.calcularDeudaCliente(clienteId);
        return ResponseEntity.ok(Map.of("deudaTotal", deuda));
    }

    @PostMapping("/{creditoId}/pagar")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> pagarCredito(
            @PathVariable Long creditoId,
            @RequestParam Long usuarioId) {
        creditoService.pagarCredito(creditoId, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Crédito pagado exitosamente"));
    }

    @PostMapping("/cliente/{clienteId}/pagar-todo")
    @PreAuthorize("hasAnyRole('DUENO', 'CAJERO')")
    public ResponseEntity<Map<String, String>> pagarTodoCliente(
            @PathVariable Long clienteId,
            @RequestParam Long usuarioId) {
        creditoService.pagarTodoCliente(clienteId, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Todos los créditos pagados exitosamente"));
    }
}
