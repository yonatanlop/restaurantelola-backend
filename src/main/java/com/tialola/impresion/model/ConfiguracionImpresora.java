package com.tialola.impresion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_impresora")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionImpresora {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "tipo_impresora", nullable = false, length = 20)
    private String tipoImpresora; // TERMICA, MATRICIAL, PDF
    
    @Column(name = "nombre_impresora", length = 200)
    private String nombreImpresora; // Nombre del sistema
    
    @Column(name = "ancho_papel")
    private Integer anchoPapel = 80; // mm
    
    @Column(name = "caracteres_linea")
    private Integer caracteresLinea = 42;
    
    @Column(name = "imprimir_logo")
    private Boolean imprimirLogo = false;
    
    @Column(name = "ruta_logo")
    private String rutaLogo;
    
    @Column(name = "texto_encabezado", columnDefinition = "TEXT")
    private String textoEncabezado;
    
    @Column(name = "texto_pie", columnDefinition = "TEXT")
    private String textoPie;
    
    @Column(name = "auto_cortar")
    private Boolean autoCortar = true;
    
    @Column(name = "copias_comanda")
    private Integer copiasComanda = 1;
    
    @Column(name = "copias_ticket")
    private Integer copiasTicket = 1;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada = false;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}
