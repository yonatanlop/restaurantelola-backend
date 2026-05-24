package com.tialola.impresion.service;

import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.model.VentaDetalle;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import com.tialola.impresion.dto.TicketDTO;
import com.tialola.impresion.model.ColaImpresion;
import com.tialola.impresion.model.ConfiguracionImpresora;
import com.tialola.impresion.repository.ColaImpresionRepository;
import com.tialola.impresion.repository.ConfiguracionImpresoraRepository;
import com.tialola.menu.model.Plato;
import com.tialola.menu.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpresionService {

    private final VentaRepository ventaRepository;
    private final PlatoRepository platoRepository;
    private final ConfiguracionImpresoraRepository configuracionRepository;
    private final ColaImpresionRepository colaRepository;
    private final FormateadorTicketService formateadorService;

    @Transactional
    public void encolarTicketVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        ConfiguracionImpresora config = obtenerImpresoraPredeterminada();
        TicketDTO ticket = convertirVentaATicket(venta);
        String contenido = formateadorService.formatearTicketVenta(ticket, config);

        ColaImpresion cola = new ColaImpresion();
        cola.setTipoDocumento("VENTA");
        cola.setReferenciaId(ventaId);
        cola.setImpresoraId(config.getId());
        cola.setEstado("PENDIENTE");
        cola.setContenidoTicket(contenido);

        colaRepository.save(cola);
        log.info("Ticket de venta #{} encolado para impresión", ventaId);
    }

    @Transactional
    public void encolarComanda(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        ConfiguracionImpresora config = obtenerImpresoraPredeterminada();
        TicketDTO ticket = convertirVentaATicket(venta);
        String contenido = formateadorService.formatearComanda(ticket, config);

        ColaImpresion cola = new ColaImpresion();
        cola.setTipoDocumento("COMANDA");
        cola.setReferenciaId(ventaId);
        cola.setImpresoraId(config.getId());
        cola.setEstado("PENDIENTE");
        cola.setContenidoTicket(contenido);

        colaRepository.save(cola);
        log.info("Comanda #{} encolada para impresión", ventaId);
    }

    @Transactional
    public void procesarColaPendiente() {
        List<ColaImpresion> pendientes = colaRepository.findByEstadoOrderByFechaCreacionAsc("PENDIENTE");
        
        for (ColaImpresion cola : pendientes) {
            try {
                procesarImpresion(cola);
            } catch (Exception e) {
                log.error("Error procesando impresión #{}: {}", cola.getId(), e.getMessage());
                cola.setEstado("ERROR");
                cola.setIntentos(cola.getIntentos() + 1);
                cola.setMensajeError(e.getMessage());
                colaRepository.save(cola);
            }
        }
    }

    private void procesarImpresion(ColaImpresion cola) throws Exception {
        cola.setEstado("IMPRIMIENDO");
        colaRepository.save(cola);

        ConfiguracionImpresora config = configuracionRepository.findById(cola.getImpresoraId())
                .orElseThrow(() -> new RuntimeException("Configuración de impresora no encontrada"));

        if ("PDF".equals(config.getTipoImpresora())) {
            // Simular impresión a PDF (guardar en archivo)
            log.info("Simulando impresión a PDF: {}", cola.getContenidoTicket());
        } else {
            imprimirEnImpresora(cola.getContenidoTicket(), config);
        }

        cola.setEstado("COMPLETADO");
        cola.setFechaImpresion(LocalDateTime.now());
        colaRepository.save(cola);
        
        log.info("Impresión #{} completada exitosamente", cola.getId());
    }

    private void imprimirEnImpresora(String contenido, ConfiguracionImpresora config) throws Exception {
        try {
            // Buscar la impresora
            PrintService printService = null;
            
            if (config.getNombreImpresora() != null && !config.getNombreImpresora().isEmpty()) {
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService service : services) {
                    if (service.getName().equalsIgnoreCase(config.getNombreImpresora())) {
                        printService = service;
                        break;
                    }
                }
            }
            
            if (printService == null) {
                printService = PrintServiceLookup.lookupDefaultPrintService();
            }
            
            if (printService == null) {
                throw new RuntimeException("No se encontró ninguna impresora disponible");
            }

            // Crear el trabajo de impresión
            DocPrintJob job = printService.createPrintJob();
            byte[] bytes = contenido.getBytes("UTF-8");
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);
            
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            
            job.print(doc, attrs);
            log.info("Documento enviado a impresora: {}", printService.getName());
            
        } catch (Exception e) {
            log.error("Error al imprimir: {}", e.getMessage());
            throw new RuntimeException("Error al imprimir: " + e.getMessage(), e);
        }
    }

    private TicketDTO convertirVentaATicket(Venta venta) {
        List<TicketDTO.ItemTicket> items = venta.getDetalles().stream()
                .map(detalle -> {
                    Plato plato = platoRepository.findById(detalle.getPlatoId()).orElse(null);
                    return TicketDTO.ItemTicket.builder()
                            .nombrePlato(plato != null ? plato.getNombre() : "Plato #" + detalle.getPlatoId())
                            .cantidad(detalle.getCantidad())
                            .precioUnitario(detalle.getPrecioUnitario())
                            .subtotal(detalle.getSubtotal())
                            .notas(detalle.getNotas())
                            .build();
                })
                .collect(Collectors.toList());

        return TicketDTO.builder()
                .tipoTicket("VENTA")
                .ventaId(venta.getId())
                .fecha(venta.getFecha())
                .cajero(venta.getCajero() != null ? venta.getCajero() : "Sistema")
                .items(items)
                .subtotal(venta.getSubtotal())
                .impuestos(venta.getImpuestos())
                .propina(venta.getPropina())
                .total(venta.getTotal())
                .metodoPago(venta.getMetodoPago())
                .notas(venta.getNotas())
                .build();
    }

    private ConfiguracionImpresora obtenerImpresoraPredeterminada() {
        return configuracionRepository.findByEsPredeterminadaTrueAndActivoTrue()
                .orElseGet(() -> {
                    // Crear configuración por defecto si no existe
                    ConfiguracionImpresora config = new ConfiguracionImpresora();
                    config.setNombre("Impresora Predeterminada");
                    config.setTipoImpresora("PDF");
                    config.setAnchoPapel(80);
                    config.setCaracteresLinea(42);
                    config.setTextoEncabezado("RESTAURANTE DOÑA LOLA");
                    config.setTextoPie("Visitenos en www.restaurantelola.com");
                    config.setActivo(true);
                    config.setEsPredeterminada(true);
                    return configuracionRepository.save(config);
                });
    }

    public List<ColaImpresion> obtenerColaPendiente() {
        return colaRepository.findByEstadoOrderByFechaCreacionAsc("PENDIENTE");
    }

    @Transactional
    public void reintentarImpresion(Long colaId) {
        ColaImpresion cola = colaRepository.findById(colaId)
                .orElseThrow(() -> new RuntimeException("Registro de cola no encontrado"));
        
        cola.setEstado("PENDIENTE");
        cola.setMensajeError(null);
        colaRepository.save(cola);
        
        procesarColaPendiente();
    }
}
