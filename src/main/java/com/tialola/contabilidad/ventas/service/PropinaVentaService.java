package com.tialola.contabilidad.ventas.service;

import com.tialola.contabilidad.ventas.dto.PropinaEstadoDTO;
import com.tialola.contabilidad.ventas.model.PropinaVenta;
import com.tialola.contabilidad.ventas.model.Venta;
import com.tialola.contabilidad.ventas.repository.PropinaVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PropinaVentaService {

    private final PropinaVentaRepository propinaVentaRepository;

    @Value("${propinas.enabled:false}")
    private boolean propinasHabilitadas;

    @Transactional
    public void prepararRegistroParaVenta(Venta venta) {
        if (venta == null || venta.getId() == null) {
            return;
        }

        BigDecimal propina = venta.getPropina() != null ? venta.getPropina() : BigDecimal.ZERO;
        PropinaVenta registro = propinaVentaRepository.findByVentaId(venta.getId())
                .orElseGet(() -> {
                    PropinaVenta nuevo = new PropinaVenta();
                    nuevo.setVentaId(venta.getId());
                    return nuevo;
                });

        registro.setMontoRegistrado(propina);
        registro.setEstado(propinasHabilitadas ? "PENDIENTE" : "DESHABILITADA");
        registro.setNotas(propinasHabilitadas
                ? "La propina está lista para ser distribuida."
                : "Estructura creada. Activa la bandera propinas.enabled para utilizarla.");

        propinaVentaRepository.save(registro);
    }

    public PropinaEstadoDTO obtenerEstado() {
        return PropinaEstadoDTO.builder()
                .habilitada(propinasHabilitadas)
                .mensaje(propinasHabilitadas
                        ? "La captura de propinas por venta está habilitada."
                        : "La estructura de propinas está lista pero continúa deshabilitada.")
                .proximoPaso("Actualiza propinas.enabled=true para reflejar propinas en tickets y reportes.")
                .version("1.0.0")
                .build();
    }
}

