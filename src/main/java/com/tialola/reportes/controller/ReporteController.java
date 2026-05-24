package com.tialola.reportes.controller;

import com.tialola.reportes.service.ReporteVentasService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteVentasService reporteService;
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> descargarReporteExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String tipoComida) {
        
        try {
            byte[] excelBytes = reporteService.generarReporteExcel(inicio, fin, metodoPago, tipoComida);
            
            String filename = "reporte_ventas_" + LocalDateTime.now().format(FILE_FORMATTER) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelBytes.length);
            
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> descargarReportePDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String tipoComida) {
        
        try {
            byte[] pdfBytes = reporteService.generarReportePDF(inicio, fin, metodoPago, tipoComida);
            
            String filename = "reporte_ventas_" + LocalDateTime.now().format(FILE_FORMATTER) + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
