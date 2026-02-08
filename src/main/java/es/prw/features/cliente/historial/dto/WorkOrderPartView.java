package es.prw.features.cliente.historial.dto;

import java.math.BigDecimal;

public record WorkOrderPartView(
        Long idWop,
        Long partId,
        String sku,
        String nombre,
        BigDecimal cantidad,
        BigDecimal precioUnit,
        BigDecimal total,
        String notes
) {}
