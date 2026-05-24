package com.tialola.caja.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio para controlar la caja registradora mediante comandos ESC/POS
 */
@Service
@Slf4j
public class CajaRegistradoraService {

    @Value("${caja.registradora.puerto:COM1}")
    private String puerto;

    @Value("${caja.registradora.habilitada:true}")
    private boolean habilitada;

    // Comando ESC/POS estándar para abrir cajón: ESC p m t1 t2
    // ESC = 27 (0x1B), p = 112 (0x70)
    // m = pin del cajón (0 o 1)
    // t1 = tiempo ON en ms (múltiplos de 2ms)
    // t2 = tiempo OFF en ms (múltiplos de 2ms)
    private static final byte[] COMANDO_ABRIR_CAJON = {
        0x1B, 0x70, 0x00, 0x19, 0x19  // ESC p 0 25 25 (100ms ON, 100ms OFF)
    };

    /**
     * Abre el cajón de la caja registradora
     * @return true si se envió el comando exitosamente
     */
    public boolean abrirCajon() {
        if (!habilitada) {
            log.info("Caja registradora deshabilitada en configuración");
            return false;
        }

        try {
            log.info("Intentando abrir cajón en puerto: {}", puerto);
            
            // Intentar diferentes métodos según el sistema operativo
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                return abrirCajonWindows();
            } else {
                return abrirCajonLinux();
            }
            
        } catch (Exception e) {
            log.error("Error al abrir cajón de caja registradora: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Abre el cajón en Windows
     */
    private boolean abrirCajonWindows() {
        try {
            // En Windows, intentar escribir directamente al puerto COM
            Path puertoPath = Paths.get("\\\\.\\" + puerto);
            
            if (!Files.exists(puertoPath)) {
                log.warn("Puerto {} no encontrado", puerto);
                return false;
            }

            try (OutputStream out = new FileOutputStream(puertoPath.toFile())) {
                out.write(COMANDO_ABRIR_CAJON);
                out.flush();
                log.info("Comando enviado exitosamente al puerto {}", puerto);
                return true;
            }
            
        } catch (Exception e) {
            log.error("Error al abrir cajón en Windows: {}", e.getMessage());
            
            // Método alternativo: usar comando MODE y COPY
            try {
                return abrirCajonWindowsAlternativo();
            } catch (Exception e2) {
                log.error("Error en método alternativo: {}", e2.getMessage());
                return false;
            }
        }
    }

    /**
     * Método alternativo para Windows usando comandos del sistema
     */
    private boolean abrirCajonWindowsAlternativo() {
        try {
            // Configurar puerto
            ProcessBuilder pb1 = new ProcessBuilder("cmd", "/c", 
                "mode", puerto + ":", "BAUD=9600", "PARITY=N", "DATA=8", "STOP=1");
            Process p1 = pb1.start();
            p1.waitFor();

            // Crear archivo temporal con el comando
            Path tempFile = Files.createTempFile("cajon", ".bin");
            Files.write(tempFile, COMANDO_ABRIR_CAJON);

            // Enviar comando al puerto
            ProcessBuilder pb2 = new ProcessBuilder("cmd", "/c", 
                "copy", "/b", tempFile.toString(), "\\\\.\\" + puerto);
            Process p2 = pb2.start();
            int exitCode = p2.waitFor();

            // Limpiar archivo temporal
            Files.deleteIfExists(tempFile);

            log.info("Comando alternativo ejecutado con código: {}", exitCode);
            return exitCode == 0;
            
        } catch (Exception e) {
            log.error("Error en método alternativo Windows: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Abre el cajón en Linux/Unix
     */
    private boolean abrirCajonLinux() {
        try {
            // En Linux, el puerto serial suele ser /dev/ttyS0, /dev/ttyUSB0, etc.
            String puertoLinux = puerto.replace("COM", "/dev/ttyS");
            if (puerto.equals("COM1")) {
                puertoLinux = "/dev/ttyS0";
            } else if (puerto.equals("COM2")) {
                puertoLinux = "/dev/ttyS1";
            }

            Path puertoPath = Paths.get(puertoLinux);
            
            if (!Files.exists(puertoPath)) {
                log.warn("Puerto {} no encontrado", puertoLinux);
                return false;
            }

            try (OutputStream out = new FileOutputStream(puertoPath.toFile())) {
                out.write(COMANDO_ABRIR_CAJON);
                out.flush();
                log.info("Comando enviado exitosamente al puerto {}", puertoLinux);
                return true;
            }
            
        } catch (Exception e) {
            log.error("Error al abrir cajón en Linux: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si la caja registradora está habilitada
     */
    public boolean isHabilitada() {
        return habilitada;
    }

    /**
     * Obtiene el puerto configurado
     */
    public String getPuerto() {
        return puerto;
    }
}
