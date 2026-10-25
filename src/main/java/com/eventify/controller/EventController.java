package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@Tag(
        name = "Eventos",
        description = "Operaciones del catálogo de eventos optimizadas con Proyecciones DTO (Records), paginación con Slice y Borrado Lógico (Soft Delete)"
)
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo evento",
            description = "Crea un evento vinculado obligatoriamente a una sede física (Venue) y opcionalmente a un conjunto de categorías."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Evento creado exitosamente"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o sede no asignada"
    )
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.save(event), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Listado masivo y motor de búsqueda filtrado",
            description = """
                Retorna un fragmento (`Slice`) de eventos proyectados en el Record `EventSummaryDTO`.
                Permite búsquedas combinadas, parciales e insensibles a mayúsculas/minúsculas.
                Garantiza orden cronológico descendente y excluye automáticamente eventos inactivos (Soft Delete con @SQLRestriction).
                Al utilizar `Slice`, no se ejecuta la costosa consulta `COUNT(*)`.
            """
    )
    public ResponseEntity<Slice<EventSummaryDTO>> getEvents(
            @Parameter(description = "Búsqueda parcial o exacta por ciudad de la sede (ej. 'bog', 'Bogotá')", example = "Bogotá")
            @RequestParam(required = false) String ciudad,

            @Parameter(description = "Búsqueda parcial o exacta por nombre de categoría (ej. 'rock', 'Conciertos')", example = "Tecnología")
            @RequestParam(required = false) String categoria,

            @Parameter(description = "Fecha inicial del rango de búsqueda (YYYY-MM-DD)", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @Parameter(description = "Fecha final del rango de búsqueda (YYYY-MM-DD)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @ParameterObject Pageable pageable
    ) {
        Slice<EventSummaryDTO> result = eventService.findFilteredSummarySlice(ciudad, categoria, fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar detalle de un evento por ID",
            description = "Obtiene la entidad completa con sus relaciones (Venue y Categorías). Solo encuentra eventos con activo=true."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Evento encontrado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Evento no encontrado o dado de baja lógica"
    )
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un evento existente",
            description = "Actualiza los campos básicos y vínculos relacionales del evento."
    )
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un evento (Borrado Lógico / Soft Delete)",
            description = """
                Por políticas de trazabilidad y auditoría, el registro **no se elimina físicamente** de la base de datos.
                Se actualiza su estado a inactivo (`activo = false`).
                Las consultas posteriores lo excluirán automáticamente mediante `@SQLRestriction("activo = true")`.
            """
    )
    @ApiResponse(
            responseCode = "204",
            description = "Evento desactivado exitosamente (Soft Delete)"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Evento no encontrado"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
