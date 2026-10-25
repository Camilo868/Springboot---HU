package com.eventify.dto;

import java.time.LocalDate;

/**
 * Record optimizado para listados masivos (Proyección DTO plana).
 * Evita la sobrecarga de memoria de entidades JPA completas.
 */
public record EventSummaryDTO(
        Long id,
        String nombre,
        LocalDate fecha,
        String descripcion,
        String nombreLugar,
        String ciudad
) {
}
