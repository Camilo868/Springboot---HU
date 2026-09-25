package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "CRUD de Eventos con paginación")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo evento")
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.save(event), HttpStatus.CREATED); // 201 Creado
    }

    // Aqui está la Paginación
    @GetMapping
    @Operation(summary = "Consultar catálogo de eventos paginado")
    public ResponseEntity<Page<Event>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(eventService.findAll(pageable)); // 200 OK
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un evento por ID")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id)); // 200 OK o 404 si falla
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento existente")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id, event)); // 200 OK o 404 si falla
    }

    // Escenario 4: Borrado exitoso
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento (Borrado físico)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build(); // 204 Sin contenido
    }
}