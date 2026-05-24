package com.tialola.credito.service;

import com.tialola.credito.dto.ClienteDTO;
import com.tialola.credito.dto.CreditoDTO;
import com.tialola.credito.model.Cliente;
import com.tialola.credito.model.Credito;
import com.tialola.credito.repository.ClienteRepository;
import com.tialola.credito.repository.CreditoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditoService {

    private final ClienteRepository clienteRepository;
    private final CreditoRepository creditoRepository;

    // Clientes
    public List<ClienteDTO> obtenerClientesActivos() {
        List<Cliente> clientes = clienteRepository.findByActivoTrueOrderByNombreAsc();
        return clientes.stream()
                .map(this::convertirAClienteDTO)
                .collect(Collectors.toList());
    }

    public List<Cliente> buscarClientes(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setDireccion(clienteActualizado.getDireccion());
        cliente.setNotas(clienteActualizado.getNotas());
        
        return clienteRepository.save(cliente);
    }

    // Créditos
    @Transactional
    public Credito registrarCredito(CreditoDTO dto) {
        Credito credito = new Credito();
        credito.setClienteId(dto.getClienteId());
        credito.setVentaId(dto.getVentaId());
        credito.setValorPedido(dto.getValorPedido());
        credito.setDescripcion(dto.getDescripcion());
        credito.setNotas(dto.getNotas());
        credito.setUsuarioRegistroId(dto.getUsuarioRegistroId());
        credito.setPagado(false);
        
        return creditoRepository.save(credito);
    }

    public List<Credito> obtenerCreditosPorCliente(Long clienteId) {
        return creditoRepository.findByClienteIdOrderByFechaPedidoDesc(clienteId);
    }

    public List<Credito> obtenerCreditosPendientes() {
        return creditoRepository.findByPagadoFalseOrderByFechaPedidoAsc();
    }

    public List<Credito> obtenerCreditosPendientesPorCliente(Long clienteId) {
        return creditoRepository.findByClienteIdAndPagadoFalseOrderByFechaPedidoAsc(clienteId);
    }

    public BigDecimal calcularDeudaCliente(Long clienteId) {
        BigDecimal deuda = creditoRepository.calcularDeudaTotal(clienteId);
        return deuda != null ? deuda : BigDecimal.ZERO;
    }

    @Transactional
    public void pagarCredito(Long creditoId, Long usuarioPagoId) {
        Credito credito = creditoRepository.findById(creditoId)
                .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
        
        if (credito.getPagado()) {
            throw new RuntimeException("Este crédito ya está pagado");
        }
        
        credito.setPagado(true);
        credito.setFechaPago(LocalDateTime.now());
        credito.setUsuarioPagoId(usuarioPagoId);
        
        creditoRepository.save(credito);
    }

    @Transactional
    public void pagarTodoCliente(Long clienteId, Long usuarioPagoId) {
        List<Credito> creditosPendientes = creditoRepository
                .findByClienteIdAndPagadoFalseOrderByFechaPedidoAsc(clienteId);
        
        for (Credito credito : creditosPendientes) {
            credito.setPagado(true);
            credito.setFechaPago(LocalDateTime.now());
            credito.setUsuarioPagoId(usuarioPagoId);
        }
        
        creditoRepository.saveAll(creditosPendientes);
    }

    private ClienteDTO convertirAClienteDTO(Cliente cliente) {
        BigDecimal deuda = calcularDeudaCliente(cliente.getId());
        List<Credito> pendientes = obtenerCreditosPendientesPorCliente(cliente.getId());
        
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .notas(cliente.getNotas())
                .deudaTotal(deuda)
                .creditosPendientes(pendientes.size())
                .build();
    }
}
