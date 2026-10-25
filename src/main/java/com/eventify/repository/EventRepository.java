package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 1. Proyección DTO con Record y Slice (sin COUNT(*)) ordenado por fecha DESC
    @Query("""
        SELECT new com.eventify.dto.EventSummaryDTO(
            e.id,
            e.nombre,
            e.fecha,
            e.descripcion,
            v.nombre,
            v.ciudad
        )
        FROM Event e
        JOIN e.venue v
        ORDER BY e.fecha DESC
    """)
    Slice<EventSummaryDTO> findAllSummarySlice(Pageable pageable);

    // 2. Motor de búsqueda avanzado: Búsqueda flexible, parcial e insensible a mayúsculas
    // Filtrado opcional por ciudad, categoría y rango de fechas con proyección a EventSummaryDTO
    @Query("""
        SELECT DISTINCT new com.eventify.dto.EventSummaryDTO(
            e.id,
            e.nombre,
            e.fecha,
            e.descripcion,
            v.nombre,
            v.ciudad
        )
        FROM Event e
        JOIN e.venue v
        LEFT JOIN e.categorias c
        WHERE (:ciudad IS NULL OR LOWER(v.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
          AND (:categoria IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
          AND (:fechaInicio IS NULL OR e.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR e.fecha <= :fechaFin)
        ORDER BY e.fecha DESC
    """)
    Slice<EventSummaryDTO> findFilteredSummarySlice(
            @Param("ciudad") String ciudad,
            @Param("categoria") String categoria,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    // 3. Solución al problema N+1 con EntityGraph: Carga ansiosa de Venue y Categorias en un solo paso
    @EntityGraph(attributePaths = {"venue", "categorias"})
    @Query("SELECT e FROM Event e ORDER BY e.fecha DESC")
    Slice<Event> findAllWithRelationsSlice(Pageable pageable);

    // 4. Consulta de lista completa con EntityGraph ordenada por fecha DESC
    @EntityGraph(attributePaths = {"venue", "categorias"})
    @Query("SELECT e FROM Event e ORDER BY e.fecha DESC")
    List<Event> findAllWithRelations();

    // 5. Buscar por ID trayendo relaciones
    @EntityGraph(attributePaths = {"venue", "categorias"})
    Optional<Event> findById(Long id);
}
