package com.tialola.impresion.service;

import com.tialola.impresion.dto.TicketDTO;
import com.tialola.impresion.model.ConfiguracionImpresora;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FormateadorTicketService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public String formatearTicketVenta(TicketDTO ticket, ConfiguracionImpresora config) {
        StringBuilder sb = new StringBuilder();
        int ancho = config.getCaracteresLinea();

        // Encabezado
        if (config.getTextoEncabezado() != null && !config.getTextoEncabezado().isEmpty()) {
            sb.append(centrar(config.getTextoEncabezado(), ancho)).append("\n");
            sb.append(linea(ancho)).append("\n");
        }

        // Información de la venta
        sb.append(centrar("TICKET DE VENTA", ancho)).append("\n");
        sb.append(linea(ancho)).append("\n");
        sb.append(String.format("Ticket #: %d\n", ticket.getVentaId()));
        sb.append(String.format("Fecha: %s\n", ticket.getFecha().format(FORMATO_FECHA)));
        sb.append(String.format("Cajero: %s\n", ticket.getCajero()));
        sb.append(linea(ancho)).append("\n");

        // Items
        sb.append(String.format("%-20s %3s %8s %8s\n", "PRODUCTO", "Cant", "P.Unit", "Total"));
        sb.append(linea(ancho)).append("\n");

        for (TicketDTO.ItemTicket item : ticket.getItems()) {
            String nombre = truncar(item.getNombrePlato(), 20);
            sb.append(String.format("%-20s %3d %8.2f %8.2f\n",
                    nombre,
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getSubtotal()));
            
            if (item.getNotas() != null && !item.getNotas().isEmpty()) {
                sb.append(String.format("  Nota: %s\n", item.getNotas()));
            }
        }

        sb.append(linea(ancho)).append("\n");

        // Totales
        sb.append(alinearDerecha(String.format("Subtotal: $%,.2f", ticket.getSubtotal()), ancho)).append("\n");
        
        if (ticket.getImpuestos() != null && ticket.getImpuestos().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(alinearDerecha(String.format("Impuestos: $%,.2f", ticket.getImpuestos()), ancho)).append("\n");
        }
        
        if (ticket.getPropina() != null && ticket.getPropina().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(alinearDerecha(String.format("Propina: $%,.2f", ticket.getPropina()), ancho)).append("\n");
        }
        
        sb.append(linea(ancho)).append("\n");
        sb.append(alinearDerecha(String.format("TOTAL: $%,.2f", ticket.getTotal()), ancho)).append("\n");
        sb.append(linea(ancho)).append("\n");

        // Método de pago
        sb.append(String.format("Metodo de pago: %s\n", ticket.getMetodoPago()));

        // Pie de página
        if (config.getTextoPie() != null && !config.getTextoPie().isEmpty()) {
            sb.append("\n");
            sb.append(centrar(config.getTextoPie(), ancho)).append("\n");
        }

        sb.append("\n");
        sb.append(centrar("¡Gracias por su compra!", ancho)).append("\n");
        sb.append("\n\n");

        return sb.toString();
    }

    public String formatearComanda(TicketDTO ticket, ConfiguracionImpresora config) {
        StringBuilder sb = new StringBuilder();
        int ancho = config.getCaracteresLinea();

        // Encabezado
        sb.append(centrar("*** COMANDA ***", ancho)).append("\n");
        sb.append(linea(ancho)).append("\n");
        sb.append(String.format("Orden #: %d\n", ticket.getVentaId()));
        sb.append(String.format("Fecha: %s\n", ticket.getFecha().format(FORMATO_FECHA)));
        sb.append(linea(ancho)).append("\n\n");

        // Items
        for (TicketDTO.ItemTicket item : ticket.getItems()) {
            sb.append(String.format("[ %d ] %s\n", item.getCantidad(), item.getNombrePlato()));
            
            if (item.getNotas() != null && !item.getNotas().isEmpty()) {
                sb.append(String.format("     ** %s **\n", item.getNotas()));
            }
            sb.append("\n");
        }

        sb.append(linea(ancho)).append("\n");
        sb.append(String.format("Total items: %d\n", 
                ticket.getItems().stream().mapToInt(TicketDTO.ItemTicket::getCantidad).sum()));
        sb.append("\n\n");

        return sb.toString();
    }

    private String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }
        int espacios = (ancho - texto.length()) / 2;
        return " ".repeat(espacios) + texto;
    }

    private String alinearDerecha(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }
        int espacios = ancho - texto.length();
        return " ".repeat(espacios) + texto;
    }

    private String linea(int ancho) {
        return "-".repeat(ancho);
    }

    private String truncar(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
}
