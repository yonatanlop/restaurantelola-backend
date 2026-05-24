package com.tialola.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

/**
 * Configuración para monitorear el pool de conexiones HikariCP
 * Útil para detectar problemas de conexión en hardware limitado
 * 
 * OPCIONAL: Puedes comentar @EnableScheduling si no quieres logs periódicos
 */
@Configuration
@EnableScheduling
public class HikariMonitorConfig {
    
    private static final Logger log = LoggerFactory.getLogger(HikariMonitorConfig.class);
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Se ejecuta cuando la aplicación termina de iniciar
     * Muestra el estado inicial del pool
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("========================================");
        log.info("Aplicación iniciada correctamente");
        log.info("========================================");
        mostrarEstadoPool();
    }
    
    /**
     * Monitorea el pool cada 2 minutos durante operación normal
     * Puedes ajustar el intervalo o comentar este método si genera muchos logs
     */
    @Scheduled(fixedRate = 120000) // Cada 2 minutos
    public void monitorearPoolPeriodico() {
        mostrarEstadoPool();
    }
    
    /**
     * Muestra estadísticas del pool de conexiones
     */
    private void mostrarEstadoPool() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) dataSource;
            HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
            
            int activas = pool.getActiveConnections();
            int inactivas = pool.getIdleConnections();
            int total = pool.getTotalConnections();
            int esperando = pool.getThreadsAwaitingConnection();
            
            // Log normal si todo está bien
            if (esperando == 0 && activas < 5) {
                log.info("Pool HikariCP - Estado: OK | Activas: {} | Inactivas: {} | Total: {} | Esperando: {}",
                    activas, inactivas, total, esperando);
            }
            // Log de advertencia si hay threads esperando
            else if (esperando > 0) {
                log.warn("⚠️ Pool HikariCP - ALERTA: {} threads esperando conexión | Activas: {} | Inactivas: {} | Total: {}",
                    esperando, activas, inactivas, total);
            }
            // Log de advertencia si el pool está saturado
            else if (activas >= 5) {
                log.warn("⚠️ Pool HikariCP - Pool saturado | Activas: {} | Inactivas: {} | Total: {}",
                    activas, inactivas, total);
            }
        } else {
            log.debug("DataSource no es HikariCP, no se puede monitorear");
        }
    }
}
