package com.tialola.reportes.service;

import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteVentasService {
    
    private final VentaRepository ventaRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formatea un BigDecimal como moneda sin decimales innecesarios y con separadores de miles
     * Ejemplos: 15500.00 -> $15,500 | 7750.50 -> $7,750.50
     */
    private String formatearMoneda(BigDecimal valor) {
        if (valor == null) {
            return "$0";
        }
        // Si tiene decimales significativos, mostrarlos
        if (valor.stripTrailingZeros().scale() > 0) {
            return String.format("$%,.2f", valor);
        }
        // Si es entero, no mostrar decimales
        return String.format("$%,.0f", valor);
    }
    
    public byte[] generarReporteExcel(LocalDateTime inicio, LocalDateTime fin, 
                                     String metodoPago, String tipoComida) throws IOException {
        List<Venta> ventas = filtrarVentas(inicio, fin, metodoPago, tipoComida);
        
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Reporte de Ventas");
            
            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            // Encabezado
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Fecha", "Cajero", "Subtotal", "Impuestos", 
                               "Propina", "Total", "Método Pago", "Estado"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos
            int rowNum = 1;
            BigDecimal totalGeneral = BigDecimal.ZERO;
            
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(venta.getId());
                
                Cell dateCell = row.createCell(1);
                dateCell.setCellValue(venta.getFecha().format(FORMATTER));
                dateCell.setCellStyle(dateStyle);
                
                row.createCell(2).setCellValue("Usuario " + venta.getUsuarioId());
                
                Cell subtotalCell = row.createCell(3);
                subtotalCell.setCellValue(venta.getSubtotal().doubleValue());
                subtotalCell.setCellStyle(currencyStyle);
                
                Cell impuestosCell = row.createCell(4);
                impuestosCell.setCellValue(venta.getImpuestos().doubleValue());
                impuestosCell.setCellStyle(currencyStyle);
                
                Cell propinaCell = row.createCell(5);
                propinaCell.setCellValue(venta.getPropina().doubleValue());
                propinaCell.setCellStyle(currencyStyle);
                
                Cell totalCell = row.createCell(6);
                totalCell.setCellValue(venta.getTotal().doubleValue());
                totalCell.setCellStyle(currencyStyle);
                
                row.createCell(7).setCellValue(venta.getMetodoPago());
                row.createCell(8).setCellValue(venta.getEstado());
                
                totalGeneral = totalGeneral.add(venta.getTotal());
            }
            
            // Fila de totales
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(5);
            totalLabelCell.setCellValue("TOTAL:");
            totalLabelCell.setCellStyle(headerStyle);
            
            Cell totalValueCell = totalRow.createCell(6);
            totalValueCell.setCellValue(totalGeneral.doubleValue());
            totalValueCell.setCellStyle(currencyStyle);
            
            // Ajustar anchos
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    public byte[] generarReportePDF(LocalDateTime inicio, LocalDateTime fin,
                                   String metodoPago, String tipoComida) throws IOException {
        List<Venta> ventas = filtrarVentas(inicio, fin, metodoPago, tipoComida);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
            
            // Título
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("REPORTE DE VENTAS")
                .setFont(com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);
            
            // Período
            com.itextpdf.layout.element.Paragraph periodo = new com.itextpdf.layout.element.Paragraph(
                "Período: " + inicio.format(FORMATTER) + " - " + fin.format(FORMATTER))
                .setFontSize(10)
                .setMarginBottom(20);
            document.add(periodo);
            
            // Tabla
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(6);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Encabezados
            String[] headers = {"ID", "Fecha", "Cajero", "Total", "Método", "Estado"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(header))
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5);
                table.addHeaderCell(cell);
            }
            
            // Datos
            BigDecimal totalGeneral = BigDecimal.ZERO;
            for (Venta venta : ventas) {
                table.addCell(String.valueOf(venta.getId()));
                table.addCell(venta.getFecha().format(FORMATTER));
                table.addCell("Usuario " + venta.getUsuarioId());
                table.addCell(formatearMoneda(venta.getTotal()));
                table.addCell(venta.getMetodoPago());
                table.addCell(venta.getEstado());
                
                totalGeneral = totalGeneral.add(venta.getTotal());
            }
            
            document.add(table);
            
            // Total
            com.itextpdf.layout.element.Paragraph totalParagraph = new com.itextpdf.layout.element.Paragraph(
                "\nTOTAL: " + formatearMoneda(totalGeneral))
                .setFont(com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD))
                .setFontSize(12)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setMarginTop(20);
            document.add(totalParagraph);
            
            document.close();
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new IOException("Error al generar PDF", e);
        }
    }
    
    private List<Venta> filtrarVentas(LocalDateTime inicio, LocalDateTime fin,
                                      String metodoPago, String tipoComida) {
        // Por ahora solo filtramos por fecha
        // Se puede extender para filtrar por método de pago y tipo de comida
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Formato con símbolo $ y separadores de miles, sin decimales innecesarios
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
