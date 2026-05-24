package com.tialola.auditoria.aspect;

import com.tialola.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;

    @AfterReturning(
        pointcut = "execution(* com.tialola.contabilidad.ventas.service.VentaService.crearVenta(..))",
        returning = "result"
    )
    public void auditarCreacionVenta(JoinPoint joinPoint, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null ? auth.getName() : "Sistema";
            
            auditoriaService.registrarAccion(
                "CREATE",
                "VENTA",
                extractId(result),
                null,
                usuario,
                "Venta creada exitosamente"
            );
        } catch (Exception e) {
            log.error("Error en auditoría de venta: {}", e.getMessage());
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.tialola.menu.service.PlatoService.actualizarPlato(..))",
        returning = "result"
    )
    public void auditarActualizacionPlato(JoinPoint joinPoint, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null ? auth.getName() : "Sistema";
            
            auditoriaService.registrarAccion(
                "UPDATE",
                "PLATO",
                extractId(result),
                null,
                usuario,
                "Plato actualizado"
            );
        } catch (Exception e) {
            log.error("Error en auditoría de plato: {}", e.getMessage());
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.tialola.inventario.service.InventarioService.ajustarCantidad(..))"
    )
    public void auditarAjusteInventario(JoinPoint joinPoint) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null ? auth.getName() : "Sistema";
            
            Object[] args = joinPoint.getArgs();
            Long insumoId = args.length > 0 ? (Long) args[0] : null;
            
            auditoriaService.registrarAccion(
                "AJUSTE",
                "INVENTARIO",
                insumoId,
                null,
                usuario,
                "Ajuste de inventario realizado"
            );
        } catch (Exception e) {
            log.error("Error en auditoría de inventario: {}", e.getMessage());
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.tialola.nomina.service.NominaDiariaService.registrarAsistencia(..))",
        returning = "result"
    )
    public void auditarRegistroNomina(JoinPoint joinPoint, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null ? auth.getName() : "Sistema";
            
            auditoriaService.registrarAccion(
                "CREATE",
                "NOMINA",
                extractId(result),
                null,
                usuario,
                "Nómina registrada"
            );
        } catch (Exception e) {
            log.error("Error en auditoría de nómina: {}", e.getMessage());
        }
    }

    @AfterThrowing(
        pointcut = "execution(* com.tialola..*Service.*(..))",
        throwing = "error"
    )
    public void auditarErrores(JoinPoint joinPoint, Throwable error) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null ? auth.getName() : "Sistema";
            
            String metodo = joinPoint.getSignature().getName();
            String clase = joinPoint.getTarget().getClass().getSimpleName();
            
            auditoriaService.registrarError(
                "ERROR",
                clase,
                null,
                null,
                usuario,
                String.format("Error en %s.%s: %s", clase, metodo, error.getMessage())
            );
        } catch (Exception e) {
            log.error("Error al auditar error: {}", e.getMessage());
        }
    }

    private Long extractId(Object object) {
        try {
            return (Long) object.getClass().getMethod("getId").invoke(object);
        } catch (Exception e) {
            return null;
        }
    }
}
